package com.mercedesbenz.hotelservice.service.impl;

import com.mercedesbenz.basedomains.dto.*;
import com.mercedesbenz.basedomains.dto.hotel.HotelDto;
import com.mercedesbenz.basedomains.dto.hotel.ReservationDto;
import com.mercedesbenz.basedomains.dto.hotel.RoomReservationFiltersDto;
import com.mercedesbenz.basedomains.exceptions.NotBookableException;
import com.mercedesbenz.basedomains.exceptions.ResourceNotFoundException;
import com.mercedesbenz.hotelservice.entity.Availability;
import com.mercedesbenz.hotelservice.entity.Hotel;
import com.mercedesbenz.hotelservice.entity.Reservation;
import com.mercedesbenz.hotelservice.entity.Room;
import com.mercedesbenz.hotelservice.repository.AvailabilityRepository;
import com.mercedesbenz.hotelservice.repository.HotelRepository;
import com.mercedesbenz.hotelservice.repository.ReservationRepository;
import com.mercedesbenz.hotelservice.repository.RoomRepository;
import com.mercedesbenz.hotelservice.service.HotelService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HotelServiceImpl implements HotelService {

    private ModelMapper modelMapper;
    private HotelRepository hotelRepository;
    private RoomRepository roomRepository;
    private AvailabilityRepository availabilityRepository;
    private ReservationRepository reservationRepository;

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

    @Override
    public ReservationDto bookRoom(UUID roomId, RoomReservationFiltersDto roomReservationFiltersDto) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("ROOM", "id", roomId.toString()));
        Availability availabilityBookable = null;
        Reservation reservation = null;

        if (room != null && room.getAvailabilities() != null) {
            for (Availability availability : room.getAvailabilities()) {
                if (availability.getStartDate() <= roomReservationFiltersDto.getStartDate() && availability.getEndDate() > roomReservationFiltersDto.getEndDate()) {
                    availabilityBookable = availability;
                }
            }
            if (availabilityBookable != null) {
                if ((availabilityBookable.getStartDate() + 1000*60*60*24) <= roomReservationFiltersDto.getStartDate() ) {
                    Availability availabilityBeforeStartReservation = new Availability();
                    availabilityBeforeStartReservation.setStartDate(availabilityBookable.getStartDate());
                    availabilityBeforeStartReservation.setEndDate(roomReservationFiltersDto.getStartDate() - 1000*60*60*24);
                    availabilityBeforeStartReservation.setRoom(room);
                    availabilityRepository.save(availabilityBeforeStartReservation);
                }
                if ((availabilityBookable.getEndDate() - 1000*60*60*24) >= roomReservationFiltersDto.getEndDate()) {
                    Availability availabilityAfterEndReservation = new Availability();
                    availabilityAfterEndReservation.setStartDate(roomReservationFiltersDto.getEndDate() + 1000*60*60*24);
                    availabilityAfterEndReservation.setEndDate(availabilityBookable.getEndDate());
                    availabilityAfterEndReservation.setRoom(room);
                    availabilityRepository.save(availabilityAfterEndReservation);
                }
                availabilityRepository.delete(availabilityBookable);
                reservation = new Reservation();
                reservation.setRoom(room);
                reservation.setStartDate(roomReservationFiltersDto.getStartDate());
                reservation.setEndDate(roomReservationFiltersDto.getEndDate());
                reservation.setStatus(Status.IN_PROGRESS);
                reservationRepository.save(reservation);
            }
        } else {
            throw new NotBookableException("ROOM", "id", roomId.toString());
        }

        return modelMapper.map(room, ReservationDto.class);
    }
}
