package com.mercedesbenz.hotelservice.service;

import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.basedomains.dto.hotel.HotelDto;
import com.mercedesbenz.basedomains.dto.hotel.ReservationDto;
import com.mercedesbenz.basedomains.dto.hotel.RoomReservationFiltersDto;

import java.util.List;
import java.util.UUID;

public interface HotelService {
    public List<HotelDto> insertAll(List<HotelDto> hotels);
    public List<HotelDto> findAll();
    public HotelDto findOne(UUID id);
    public List<ReservationDto> getAllBookings();
    public ReservationDto bookRoom(UUID roomId, RoomReservationFiltersDto roomReservationDto);
    public void cancelReservation(UUID id);
    public void updateReservationStatus(UUID id, Status status);
}
