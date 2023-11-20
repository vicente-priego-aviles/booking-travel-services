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
import com.company.hotelservice.entity.Availability;
import com.company.hotelservice.entity.Hotel;
import com.company.hotelservice.entity.Reservation;
import com.company.hotelservice.entity.Room;
import com.company.hotelservice.repository.AvailabilityRepository;
import com.company.hotelservice.repository.HotelRepository;
import com.company.hotelservice.repository.ReservationRepository;
import com.company.hotelservice.repository.RoomRepository;
import com.company.hotelservice.stream.ReservationProducer;
import com.company.hotelservice.helpers.BookingAvailabilityHelper;
import com.company.hotelservice.helpers.dto.BookingAvailabilityDto;
import com.company.hotelservice.service.APIClient;
import com.company.hotelservice.service.HotelService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final Logger LOGGER = LoggerFactory.getLogger(HotelServiceImpl.class);

    private APIClient apiClient;
    private ModelMapper modelMapper;
    private HotelRepository hotelRepository;
    private RoomRepository roomRepository;
    private AvailabilityRepository availabilityRepository;
    private ReservationRepository reservationRepository;
    private ReservationProducer reservationProducer;
    private BookingAvailabilityHelper bookingAvailabilityHelper;

    @Override
    public List<HotelDto> insertAll(List<HotelDto> hotels) {
        List<Hotel> hotelsEntity = hotels.stream().map((hotel) -> modelMapper.map(hotel, Hotel.class)).toList();
        List<Hotel> savedHotels = new ArrayList<>();
        hotelsEntity.forEach((hotel) -> {
            List<Room> rooms = hotel.getRooms();
            hotel.setRooms(null);
            hotel = hotelRepository.saveAndFlush(hotel);
            Hotel finalHotel = hotel;
            rooms = rooms.stream().peek((room) -> room.setHotel(finalHotel)).toList();
            rooms.forEach((room) -> {
                List<Availability> availabilities = room.getAvailabilities();
                room.setAvailabilities(null);
                room = roomRepository.saveAndFlush(room);
                Room finalRoom = room;
                availabilities = availabilities.stream().peek((availability) -> availability.setRoom(finalRoom)).toList();
                availabilities = availabilityRepository.saveAllAndFlush(availabilities);
                room.setAvailabilities(availabilities);
            });
            hotel.setRooms(rooms);
            savedHotels.add(hotel);
        });
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
    public HotelDto findOne(UUID id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("HOTEL", "id", id.toString()));
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
    public ReservationDto bookRoom(UUID roomId, RoomReservationFiltersDto roomReservationFiltersDto) {
        LOGGER.debug("HotelServiceImpl.bookRoom: trying to use FLIGHT-SERVICE API");
        ResponseDto reservationResponse = apiClient.bookCheckReservationID(roomReservationFiltersDto.getReservationID());
        LOGGER.debug("HotelServiceImpl.bookRoom: ended call to FLIGHT-SERVICE API");

        UUID reservationID = null;
        if (reservationResponse != null && reservationResponse.getData() != null) {
            reservationID = UUID.fromString(reservationResponse.getData().toString());
        }
        if (reservationID == null) {
            throw new NotBookableException("ROOM", "reservationID", roomReservationFiltersDto.getReservationID().toString());
        }

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("ROOM", "id", roomId.toString()));
        LOGGER.debug("HotelServiceImpl.bookRoom - 1 : room.getId(): {}", room.getId());
        LOGGER.debug("HotelServiceImpl.bookRoom - 4: room.getPeopleCapacity(): {}", room.getPeopleCapacity());
        LOGGER.debug("HotelServiceImpl.bookRoom - 2: room.getAvailabilities(): {}", room.getAvailabilities());
        LOGGER.debug("HotelServiceImpl.bookRoom - 3: room.getAvailabilities().size(): {}", room.getAvailabilities().size());
        LOGGER.debug("HotelServiceImpl.bookRoom - 4: room.getHotel(): {}", room.getHotel());
        BookingAvailabilityDto bookingAvailabilityDto = bookingAvailabilityHelper.calculateAvailabilities(room, roomReservationFiltersDto);
        Availability availabilityBeforeReservation = bookingAvailabilityDto.getAvailabilityBeforeReservation();
        Availability availabilityAfterReservation = bookingAvailabilityDto.getAvailabilityAfterReservation();
        Availability availabilityBookable = bookingAvailabilityDto.getAvailabilityBookable();
        List<Availability> availabilitiesToSaveWithRoom = bookingAvailabilityDto.getAvailabilitiesToSaveWithRoom();

        if (availabilityBeforeReservation != null) {
            availabilityBeforeReservation.setRoom(room);
            availabilitiesToSaveWithRoom.add(availabilityBeforeReservation);
            availabilityRepository.save(availabilityBeforeReservation);
        }
        if (availabilityAfterReservation != null) {
            availabilityAfterReservation.setRoom(room);
            availabilitiesToSaveWithRoom.add(availabilityAfterReservation);
            availabilityRepository.save(availabilityAfterReservation);
        }
        availabilityRepository.delete(availabilityBookable);
        room.setAvailabilities(availabilitiesToSaveWithRoom);
        room = roomRepository.save(room);

        Reservation reservation = new Reservation();
        reservation.setId(roomReservationFiltersDto.getReservationID());
        reservation.setRoom(room);
        reservation.setStartDate(roomReservationFiltersDto.getStartDate());
        reservation.setEndDate(roomReservationFiltersDto.getEndDate());
        reservation.setStatus(Status.IN_PROGRESS);
        reservationRepository.save(reservation);

        reservationProducer.send(modelMapper.map(reservation, ReservationDto.class));
        return modelMapper.map(reservation, ReservationDto.class);
    }

    public ReservationDto bookRoomCircuitBreakerFallback(UUID roomId, RoomReservationFiltersDto roomReservationFiltersDto, Throwable exception) throws Throwable {
        LOGGER.error("Exception handled by HotelServiceImpl.bookRoomCircuitBreakerFallback", exception);
        throw (exception instanceof BookingTravelException) ? exception : new ServiceException("FLIGHT-SERVICE");
    }

    @Override
    public void cancelReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Room room = roomRepository.findById(reservation.getRoom().getId()).orElse(null);
            if (room != null) {
                Availability availability = new Availability();
                availability.setStartDate(reservation.getStartDate());
                availability.setEndDate(reservation.getEndDate());
                availability.setRoom(room);
                availability = availabilityRepository.save(availability);
                List<Availability> availabilities = room.getAvailabilities();
                availabilities.add(availability);
                room.setAvailabilities(availabilities);
                roomRepository.save(room);
                reservation.setStatus(Status.CANCELLED);
                reservationRepository.save(reservation);
            } else {
                LOGGER.error("HotelServiceImpl: Invalid ROOM ID in the reservation object");
            }
        } else {
            LOGGER.error("HotelServiceImpl: Invalid RESERVATION ID was found");
        }
    }

    @Override
    public void updateReservationStatus(UUID id, Status status) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            reservation.setStatus(status);
            reservationRepository.save(reservation);
        }
    }
}
