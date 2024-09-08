package com.company.hotelservice.service.impl;

import com.company.basedomains.dto.ResponseDto;
import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.hotel.HotelDto;
import com.company.basedomains.dto.hotel.ReservationDto;
import com.company.basedomains.dto.hotel.RoomReservationFiltersDto;
import com.company.basedomains.exception.BookingTravelException;
import com.company.basedomains.exception.NotBookableException;
import com.company.basedomains.exception.ResourceNotFoundException;
import com.company.basedomains.exception.ServiceException;
import com.company.hotelservice.entity.Hotel;
import com.company.hotelservice.entity.Reservation;
import com.company.hotelservice.repository.HotelRepository;
import com.company.hotelservice.repository.ReservationRepository;
import com.company.hotelservice.service.APIClient;
import com.company.hotelservice.service.HotelService;
import com.company.hotelservice.stream.ReservationProducer;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final Logger LOGGER = LoggerFactory.getLogger(HotelServiceImpl.class);

    private APIClient apiClient;
    private ModelMapper modelMapper;
    private HotelRepository hotelRepository;
    private ReservationRepository reservationRepository;
    private ReservationProducer reservationProducer;

    @Override
    public List<HotelDto> insertAll(List<HotelDto> hotels) {
        List<Hotel> hotelsEntity = hotels.stream().map((hotel) -> modelMapper.map(hotel, Hotel.class)).toList();
        List<Hotel> savedHotels = hotelRepository.saveAll(hotelsEntity);
        return savedHotels.stream().map((hotel) -> modelMapper.map(hotel, HotelDto.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelDto> findAll() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream().map((hotel) -> modelMapper.map(hotel, HotelDto.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HotelDto findOne(String id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("HOTEL", "id", id));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getAllBookings() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationDto.class)).toList();
    }

    @Retry(name = "${spring.application.name}", fallbackMethod = "bookRoomCircuitBreakerFallback")
    @Override
    public ReservationDto bookHotel(String hotelId, RoomReservationFiltersDto roomReservationFiltersDto) {
        LOGGER.debug("HotelServiceImpl.bookRoom: trying to use FLIGHT-SERVICE API");
        ResponseDto reservationResponse = apiClient.bookCheckReservationID(roomReservationFiltersDto.getReservationID());
        LOGGER.debug("HotelServiceImpl.bookRoom: ended call to FLIGHT-SERVICE API");

        String reservationID = null;
        if (reservationResponse != null && reservationResponse.getData() != null) {
            reservationID = reservationResponse.getData().toString();
        }
        if (reservationID == null) {
            throw new NotBookableException("HOTEL", "reservationID", roomReservationFiltersDto.getReservationID());
        }

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("HOTEL", "id", hotelId));
        LOGGER.debug("HotelServiceImpl.bookRoom - 1 : room.getId(): {}", hotel.getId());
        LOGGER.debug("HotelServiceImpl.bookRoom - 2: room.getRemainingRooms(): {}", hotel.getRemainingRooms());

        if (hotel.getRemainingRooms() <= 0) {
            throw new NotBookableException("HOTEL: NO ROOMS REMAINING", "id", roomReservationFiltersDto.getReservationID());
        }
        hotel.setRemainingRooms(hotel.getRemainingRooms() - 1);
        hotel = hotelRepository.save(hotel);

        Reservation reservation = new Reservation();
        reservation.setId(roomReservationFiltersDto.getReservationID());
        reservation.setHotel(hotel);
        reservation.setStatus(Status.IN_PROGRESS);
        reservationRepository.save(reservation);

        reservationProducer.send(modelMapper.map(reservation, ReservationDto.class));
        return modelMapper.map(reservation, ReservationDto.class);
    }

    public ReservationDto bookRoomCircuitBreakerFallback(String hotelId, RoomReservationFiltersDto roomReservationFiltersDto, Throwable exception) throws Throwable {
        LOGGER.error("Exception handled by HotelServiceImpl.bookRoomCircuitBreakerFallback", exception);
        throw (exception instanceof BookingTravelException) ? exception : new ServiceException("FLIGHT-SERVICE");
    }

    @Override
    public void cancelReservation(String id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Hotel hotel = hotelRepository.findById(reservation.getHotel().getId()).orElse(null);
            if (hotel != null) {
                hotel.setRemainingRooms(hotel.getRemainingRooms() + 1);
                hotelRepository.save(hotel);

                reservation.setStatus(Status.CANCELLED);
                reservationRepository.save(reservation);
            } else {
                LOGGER.error("HotelServiceImpl: Invalid HOTEL ID in the reservation object");
            }
        } else {
            LOGGER.error("HotelServiceImpl: Invalid RESERVATION ID was found");
        }
    }

    @Override
    public void updateReservationStatus(String id, Status status) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            reservation.setStatus(status);
            reservationRepository.save(reservation);
        }
    }
}
