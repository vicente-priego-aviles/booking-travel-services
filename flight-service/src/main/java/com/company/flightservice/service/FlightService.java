package com.company.flightservice.service;

import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.flight.FlightDto;
import com.company.basedomains.dto.flight.ReservationDto;

import java.util.List;

public interface FlightService {
    public List<FlightDto> insertAll(List<FlightDto> flights);
    public List<FlightDto> findAllAvailable();
    public FlightDto findOne(String id);
    public List<ReservationDto> getAllBookings();
    public ReservationDto bookFlight(String flightId);
    public String checkReservation(String reservationID);
    public void cancelReservation(String id);
    public void updateReservationStatus(String id, Status status);
}
