package com.mercedesbenz.flightservice.service.impl;

import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.basedomains.dto.flight.FlightDto;
import com.mercedesbenz.basedomains.dto.flight.ReservationDto;
import com.mercedesbenz.basedomains.exceptions.NotBookableException;
import com.mercedesbenz.basedomains.exceptions.ResourceNotFoundException;
import com.mercedesbenz.flightservice.entity.Flight;
import com.mercedesbenz.flightservice.entity.Reservation;
import com.mercedesbenz.flightservice.repository.FlightRepository;
import com.mercedesbenz.flightservice.repository.ReservationRepository;
import com.mercedesbenz.flightservice.service.FlightService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FlightServiceImpl implements FlightService {

    private FlightRepository flightRepository;
    private ReservationRepository reservationRepository;
    private ModelMapper modelMapper;
    @Override
    public List<FlightDto> insertAll(List<FlightDto> flights) {
        Iterable<Flight> flightsEntities = flights.stream().map((flight) -> modelMapper.map(flight, Flight.class)).toList();
        List<Flight> savedFlightsEntities = flightRepository.saveAll(flightsEntities);
        return savedFlightsEntities.stream().map((flight -> modelMapper.map(flight, FlightDto.class))).toList();
    }

    @Override
    public List<FlightDto> findAllAvailable() {
        List<Flight> flights = flightRepository.findAllByRemainingSeatsGreaterThan(0L);
        return flights.stream().map(flight -> modelMapper.map(flight, FlightDto.class)).toList();
    }

    @Override
    public FlightDto findOne(UUID id) {
        Flight flight = flightRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FLIGHT", "id", id.toString()));
        return modelMapper.map(flight, FlightDto.class);
    }

    @Override
    public List<ReservationDto> getAllBookings() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationDto.class)).toList();
    }

    @Override
    public ReservationDto bookFlight(UUID flightId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new ResourceNotFoundException("FLIGHT", "id", flightId.toString()));
        Reservation reservation = null;
        if (flight != null && flight.getRemainingSeats() > 0) {
            reservation = new Reservation();
            reservation.setFlight(flight);
            reservation.setStatus(Status.IN_PROGRESS);
            reservationRepository.save(reservation);
            flight.setRemainingSeats(flight.getRemainingSeats() - 1);
            flightRepository.save(flight);
        } else {
            throw new NotBookableException("FLIGHT", "id", flightId.toString());
        }
        return modelMapper.map(reservation, ReservationDto.class);
    }
}
