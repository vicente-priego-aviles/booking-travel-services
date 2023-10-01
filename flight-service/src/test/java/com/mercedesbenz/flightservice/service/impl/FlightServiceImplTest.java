package com.mercedesbenz.flightservice.service.impl;

import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.basedomains.dto.flight.ReservationDto;
import com.mercedesbenz.basedomains.exception.ResourceNotFoundException;
import com.mercedesbenz.flightservice.entity.Flight;
import com.mercedesbenz.flightservice.entity.Reservation;
import com.mercedesbenz.flightservice.kafka.ReservationProducer;
import com.mercedesbenz.flightservice.repository.FlightRepository;
import com.mercedesbenz.flightservice.repository.ReservationRepository;
import com.mercedesbenz.flightservice.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationProducer reservationProducer;

    @InjectMocks
    private FlightServiceImpl flightService;

    @Captor
    ArgumentCaptor<Flight> argumentCaptorFlight;
    @Captor
    ArgumentCaptor<Reservation> argumentCaptorReservation;

    Flight flight;
    Reservation reservation;

    @BeforeEach
    void setUp() {
        flight = new Flight();
        flight.setId(UUID.randomUUID());
        flight.setAirline("Lufthansa");
        flight.setRemainingSeats(0L);
        flight.setCost(99L);
        flight.setReference("LU-0000");

        reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setStatus(Status.IN_PROGRESS);
        reservation.setFlight(flight);
    }

    @Test
    void bookFlight() {
        flight.setRemainingSeats(5L);

        when(flightRepository.findById(any())).thenReturn(Optional.ofNullable(flight));
        doNothing().when(reservationProducer).send(any());

        flightService.bookFlight(flight.getId());

        verify(reservationRepository).save(argumentCaptorReservation.capture());
        verify(flightRepository).save(argumentCaptorFlight.capture());

        verify(reservationRepository, times(1)).save(any());
        verify(flightRepository, times(1)).save(any());

        assertNotNull(argumentCaptorFlight.getValue());
        assertNotNull(argumentCaptorReservation.getValue());

        assertEquals(4L, argumentCaptorFlight.getValue().getRemainingSeats(), "The remainingSeats number is not correctly substracted");
        assertEquals(Status.IN_PROGRESS, argumentCaptorReservation.getValue().getStatus());
        assertEquals(flight.getId(), argumentCaptorReservation.getValue().getFlight().getId());
    }

    @Test
    void checkReservation() {
        UUID id = UUID.randomUUID();
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {

            flightService.checkReservation(id);
        });
        assertEquals("RESERVATION not found with id : " + id.toString(), exception.getMessage(), "A ResourceNotFoundException expected, but was not gotten.");
    }

    @Test
    void cancelReservation() {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservation));

        flightService.cancelReservation(UUID.randomUUID());

        verify(flightRepository).save(argumentCaptorFlight.capture());
        verify(reservationRepository).save(argumentCaptorReservation.capture());

        verify(flightRepository, times(1)).save(any());
        verify(reservationRepository, times(1)).save(any());

        assertNotNull(argumentCaptorFlight.getValue());
        assertNotNull(argumentCaptorReservation.getValue());

        assertEquals(1, argumentCaptorFlight.getValue().getRemainingSeats(), "The Remaining seats were not correctly updated.");
        assertEquals(Status.CANCELLED, argumentCaptorReservation.getValue().getStatus(), "The status should be CANCELLED and it is not.");
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    void updateReservationStatus(Status status) {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservation));

        flightService.updateReservationStatus(UUID.randomUUID(), status);

        verify(reservationRepository).save(argumentCaptorReservation.capture());

        verify(reservationRepository, times(1)).save(any());

        assertNotNull(argumentCaptorReservation.getValue());

        assertEquals(status, argumentCaptorReservation.getValue().getStatus(), "The status should be PAID but it is not.");
    }

}
