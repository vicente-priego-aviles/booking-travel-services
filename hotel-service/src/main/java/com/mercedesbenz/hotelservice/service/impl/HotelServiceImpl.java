package com.mercedesbenz.hotelservice.service.impl;

import com.mercedesbenz.basedomains.dto.*;
import com.mercedesbenz.basedomains.dto.hotel.HotelDto;
import com.mercedesbenz.basedomains.dto.hotel.ReservationDto;
import com.mercedesbenz.basedomains.dto.hotel.RoomReservationFiltersDto;
import com.mercedesbenz.basedomains.exception.NotBookableException;
import com.mercedesbenz.basedomains.exception.ResourceNotFoundException;
import com.mercedesbenz.basedomains.exception.ServiceException;
import com.mercedesbenz.hotelservice.entity.Availability;
import com.mercedesbenz.hotelservice.entity.Hotel;
import com.mercedesbenz.hotelservice.entity.Reservation;
import com.mercedesbenz.hotelservice.entity.Room;
import com.mercedesbenz.hotelservice.kafka.ReservationProducer;
import com.mercedesbenz.hotelservice.repository.AvailabilityRepository;
import com.mercedesbenz.hotelservice.repository.HotelRepository;
import com.mercedesbenz.hotelservice.repository.ReservationRepository;
import com.mercedesbenz.hotelservice.repository.RoomRepository;
import com.mercedesbenz.hotelservice.service.APIClient;
import com.mercedesbenz.hotelservice.service.HotelService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Service
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

    @Override
    public List<HotelDto> insertAll(List<HotelDto> hotels) {
        List<Hotel> hotelsEntity = hotels.stream().map((hotel) -> modelMapper.map(hotel, Hotel.class)).toList();
        List<Hotel> savedHotels = hotelRepository.saveAll(hotelsEntity);
        return savedHotels.stream().map((hotel) -> modelMapper.map(hotel, HotelDto.class)).toList();
    }

    @Override
    public List<HotelDto> findAll() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream().map((hotel) -> modelMapper.map(hotel, HotelDto.class)).toList();
    }

    @Override
    public HotelDto findOne(UUID id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("HOTEL", "id", id.toString()));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public List<ReservationDto> getAllBookings() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationDto.class)).toList();
    }

    @Retry(name = "${spring.application.name}", fallbackMethod = "bookRoomCircuitBreakerFallback")
    @Override
    public ReservationDto bookRoom(UUID roomId, RoomReservationFiltersDto roomReservationFiltersDto) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("ROOM", "id", roomId.toString()));
        Availability availabilityBookable = null;
        Reservation reservation = null;

        Calendar dateNormalizator = Calendar.getInstance();

        dateNormalizator.setTimeInMillis(roomReservationFiltersDto.getStartDate());
        dateNormalizator.set(dateNormalizator.get(Calendar.YEAR), dateNormalizator.get(Calendar.MONTH), dateNormalizator.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
        roomReservationFiltersDto.setStartDate(dateNormalizator.getTimeInMillis());

        dateNormalizator.setTimeInMillis(roomReservationFiltersDto.getEndDate());
        dateNormalizator.set(dateNormalizator.get(Calendar.YEAR), dateNormalizator.get(Calendar.MONTH), dateNormalizator.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
        roomReservationFiltersDto.setEndDate(dateNormalizator.getTimeInMillis());


        if (room != null && room.getAvailabilities() != null) {
            List<Availability> availabilitiesToSaveWithRoom = new ArrayList<>();
            for (Availability availability : room.getAvailabilities()) {
                if (availability.getStartDate() <= roomReservationFiltersDto.getStartDate() && availability.getEndDate() >= roomReservationFiltersDto.getEndDate()) {
                    availabilityBookable = availability;
                } else {
                    availabilitiesToSaveWithRoom.add(availability);
                }
            }
            if (availabilityBookable != null &&
                availabilityBookable.getStartDate() <= roomReservationFiltersDto.getStartDate() &&
                roomReservationFiltersDto.getEndDate() <= availabilityBookable.getEndDate()) {
                Availability availabilityBeforeReservation = null;
                Availability availabilityAfterReservation = null;
                Calendar updatedDate = Calendar.getInstance();

                Calendar availabilityBookableStartDate = Calendar.getInstance();
                Calendar availabilityBookableEndDate = Calendar.getInstance();
                Calendar roomReservationFiltersDtoStartDate = Calendar.getInstance();
                Calendar roomReservationFiltersDtoEndDate = Calendar.getInstance();
                availabilityBookableStartDate.setTimeInMillis(availabilityBookable.getStartDate());
                availabilityBookableEndDate.setTimeInMillis(availabilityBookable.getEndDate());
                roomReservationFiltersDtoStartDate.setTimeInMillis(roomReservationFiltersDto.getStartDate());
                roomReservationFiltersDtoEndDate.setTimeInMillis(roomReservationFiltersDto.getEndDate());

                if (availabilityBookableStartDate.get(Calendar.YEAR) == roomReservationFiltersDtoStartDate.get(Calendar.YEAR)  &&
                        availabilityBookableStartDate.get(Calendar.MONTH) == roomReservationFiltersDtoStartDate.get(Calendar.MONTH) &&
                        availabilityBookableStartDate.get(Calendar.DAY_OF_MONTH) == roomReservationFiltersDtoStartDate.get(Calendar.DAY_OF_MONTH) &&
                        availabilityBookableStartDate.get(Calendar.HOUR) == roomReservationFiltersDtoStartDate.get(Calendar.HOUR) &&
                        availabilityBookableStartDate.get(Calendar.MINUTE) == roomReservationFiltersDtoStartDate.get(Calendar.MINUTE)) {
                    availabilityAfterReservation = new Availability();
                    updatedDate.setTimeInMillis(roomReservationFiltersDto.getEndDate());
                    updatedDate.set(updatedDate.get(Calendar.YEAR), updatedDate.get(Calendar.MONTH), updatedDate.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
                    availabilityAfterReservation.setStartDate(updatedDate.getTimeInMillis());
                    if (roomReservationFiltersDtoEndDate.getTimeInMillis() <= availabilityBookableEndDate.getTimeInMillis()) {
                        updatedDate.setTimeInMillis(availabilityBookableEndDate.getTimeInMillis());
                        updatedDate.set(updatedDate.get(Calendar.YEAR), updatedDate.get(Calendar.MONTH), updatedDate.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
                        availabilityAfterReservation.setEndDate(updatedDate.getTimeInMillis());
                    } else {
                        availabilityAfterReservation = null;
                    }
                } else {
                    availabilityBeforeReservation = new Availability();
                    availabilityBeforeReservation.setStartDate(availabilityBookable.getStartDate());
                    updatedDate.setTimeInMillis(roomReservationFiltersDto.getStartDate());
                    updatedDate.set(updatedDate.get(Calendar.YEAR), updatedDate.get(Calendar.MONTH), updatedDate.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
                    availabilityBeforeReservation.setEndDate(updatedDate.getTimeInMillis());

                    if (roomReservationFiltersDtoEndDate.getTimeInMillis() < availabilityBookableEndDate.getTimeInMillis()) {
                        availabilityAfterReservation = new Availability();
                        updatedDate.setTimeInMillis(roomReservationFiltersDto.getEndDate());
                        updatedDate.set(updatedDate.get(Calendar.YEAR), updatedDate.get(Calendar.MONTH), updatedDate.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
                        availabilityAfterReservation.setStartDate(updatedDate.getTimeInMillis());
                        availabilityAfterReservation.setEndDate(availabilityBookable.getEndDate());
                    }
                }

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
                reservation = new Reservation();
                reservation.setId(roomReservationFiltersDto.getReservationID());
                reservation.setRoom(room);
                reservation.setStartDate(roomReservationFiltersDto.getStartDate());
                reservation.setEndDate(roomReservationFiltersDto.getEndDate());
                reservation.setStatus(Status.IN_PROGRESS);
                reservationRepository.save(reservation);

                reservationProducer.send(modelMapper.map(reservation, ReservationDto.class));
            } else {
                throw new NotBookableException("ROOM", "id", roomId.toString());
            }
        } else {
            throw new NotBookableException("ROOM", "id", roomId.toString());
        }
        return modelMapper.map(reservation, ReservationDto.class);
    }

    public void bookRoomCircuitBreakerFallback(Exception exception) {
        LOGGER.error("Exception handled by HotelServiceImpl.bookRoomCircuitBreakerFallback", exception);
        throw new ServiceException("FLIGHT-SERVICE");
    }

    @Override
    public void cancelReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Room room = reservation.getRoom();
            Availability availability = new Availability();
            availability.setStartDate(reservation.getStartDate());
            availability.setEndDate(reservation.getEndDate());
            availability.setRoom(room);
            availabilityRepository.save(availability);
            reservation.setStatus(Status.CANCELLED);
            reservationRepository.save(reservation);
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
