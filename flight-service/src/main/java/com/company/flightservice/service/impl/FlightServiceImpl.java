package com.company.flightservice.service.impl;

import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.flight.FlightDto;
import com.company.basedomains.dto.flight.ReservationDto;
import com.company.basedomains.exception.NotBookableException;
import com.company.basedomains.exception.ResourceNotFoundException;
import com.company.flightservice.repository.FlightRepository;
import com.company.flightservice.repository.ReservationRepository;
import com.company.flightservice.stream.ReservationProducer;
import com.company.flightservice.entity.Flight;
import com.company.flightservice.entity.Reservation;
import com.company.flightservice.service.FlightService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class FlightServiceImpl implements FlightService {

    private FlightRepository flightRepository;
    private ReservationRepository reservationRepository;
    private ModelMapper modelMapper;
    private ReservationProducer reservationProducer;
    @Override
    public List<FlightDto> insertAll(List<FlightDto> flights) {
        Iterable<Flight> flightsEntities = flights.stream().map((flight) -> modelMapper.map(flight, Flight.class)).toList();
        List<Flight> savedFlightsEntities = flightRepository.saveAll(flightsEntities);
        return savedFlightsEntities.stream().map((flight -> modelMapper.map(flight, FlightDto.class))).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDto> findAllAvailable() {
        List<Flight> flights = flightRepository.findAllByRemainingSeatsGreaterThan(0L);
        return flights.stream().map(flight -> modelMapper.map(flight, FlightDto.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FlightDto findOne(String id) {
        Flight flight = flightRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FLIGHT", "id", id));
        return modelMapper.map(flight, FlightDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getAllBookings() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationDto.class)).toList();
    }

    @Override
    public ReservationDto bookFlight(String flightId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new ResourceNotFoundException("FLIGHT", "id", flightId));
        Reservation reservation = null;
        if (flight != null && flight.getRemainingSeats() > 0) {
            reservation = new Reservation();
            reservation.setId(UUID.randomUUID().toString());
            reservation.setFlight(flight);
            reservation.setStatus(Status.IN_PROGRESS);
            reservationRepository.save(reservation);
            flight.setRemainingSeats(flight.getRemainingSeats() - 1);
            flightRepository.save(flight);

            reservationProducer.send(modelMapper.map(reservation, ReservationDto.class));
        } else {
            throw new NotBookableException("FLIGHT", "id", flightId);
        }
        return modelMapper.map(reservation, ReservationDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkReservation(String reservationID) {
        Reservation reservation = reservationRepository.findById(reservationID).orElseThrow(() -> new ResourceNotFoundException("RESERVATION", "id", reservationID));
        return reservation.getId();
    }

    @Override
    public void cancelReservation(String id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Flight flight = reservation.getFlight();
            flight.setRemainingSeats(flight.getRemainingSeats() + 1);
            flightRepository.save(flight);
            reservation.setStatus(Status.CANCELLED);
            reservationRepository.save(reservation);
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
