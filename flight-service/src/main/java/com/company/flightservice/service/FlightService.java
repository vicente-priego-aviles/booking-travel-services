package com.company.flightservice.service;

import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.flight.FlightDto;
import com.company.basedomains.dto.flight.ReservationDto;

import java.util.List;
import java.util.UUID;

public interface FlightService {
    public List<FlightDto> insertAll(List<FlightDto> flights);
    public List<FlightDto> findAllAvailable();
    public FlightDto findOne(UUID id);
    public List<ReservationDto> getAllBookings();
    public ReservationDto bookFlight(UUID flightId);
    public UUID checkReservation(UUID reservationID);
    public void cancelReservation(UUID id);
    public void updateReservationStatus(UUID id, Status status);
}
