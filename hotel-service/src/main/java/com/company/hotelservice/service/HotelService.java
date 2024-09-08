package com.company.hotelservice.service;

import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.hotel.HotelDto;
import com.company.basedomains.dto.hotel.ReservationDto;
import com.company.basedomains.dto.hotel.RoomReservationFiltersDto;

import java.util.List;
import java.util.UUID;

public interface HotelService {
    public List<HotelDto> insertAll(List<HotelDto> hotels);
    public List<HotelDto> findAll();
    public HotelDto findOne(String id);
    public List<ReservationDto> getAllBookings();
    public ReservationDto bookHotel(String hotelId, RoomReservationFiltersDto roomReservationDto);
    public void cancelReservation(String id);
    public void updateReservationStatus(String id, Status status);
}
