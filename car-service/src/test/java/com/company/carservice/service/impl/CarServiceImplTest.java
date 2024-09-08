package com.company.carservice.service.impl;

import com.company.basedomains.dto.ResponseDto;
import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.cars.CarReservationFiltersDto;
import com.company.carservice.entity.Car;
import com.company.carservice.entity.Reservation;
import com.company.carservice.repository.CarRepository;
import com.company.carservice.repository.ReservationRepository;
import com.company.carservice.service.APIClient;
import com.company.carservice.stream.ReservationProducer;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {

    @Mock
    ModelMapper modelMapper;

    @Mock
    APIClient apiClient;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationProducer reservationProducer;

    @InjectMocks
    private CarServiceImpl carService;

    @Captor
    ArgumentCaptor<Reservation> reservationArgumentCaptor;

    Car car;
    Reservation reservation;
    Reservation reservationToCancel;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setBrand("Mercedes-Benz");
        car.setModel("Clase A");
        car.setCostPerDay(5999L);
        car.setRemainingCars(10L);

        reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setCar(car);
        reservation.setStatus(Status.IN_PROGRESS);

        reservationToCancel = new Reservation();
        reservationToCancel.setId(UUID.randomUUID().toString());
        reservationToCancel.setCar(car);
        reservationToCancel.setStatus(Status.IN_PROGRESS);
    }

    @Test
    void bookCar() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id.toString());

        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));
        when(reservationRepository.save(any())).thenReturn(null);
        doNothing().when(reservationProducer).send(any());
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);

        carService.bookCar(car.getId(), carReservationFiltersDto);

        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(reservationArgumentCaptor.getValue());

        verify(apiClient, times(1)).bookCheckReservationID(any());

        assertEquals(carReservationFiltersDto.getReservationID(), reservationArgumentCaptor.getValue().getId(), "Reservation ID saved is not correct");
        assertEquals(Status.IN_PROGRESS, reservationArgumentCaptor.getValue().getStatus(), "Reservation status should be IN_PROGRESS");
    }

    @Test
    void cancelReservation() {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservationToCancel));
        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));

        carService.cancelReservation(UUID.randomUUID().toString());

        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        verify(carRepository, times(1)).save(any());
        verify(reservationRepository, times(1)).save(any());

        assertNotNull(reservationArgumentCaptor.getValue());

        assertEquals(Status.CANCELLED, reservationArgumentCaptor.getValue().getStatus(), "The status should be CANCELLED and it is not.");
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    void updateReservationStatus(Status status) {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservation));

        carService.updateReservationStatus(UUID.randomUUID().toString(), status);

        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        verify(reservationRepository, times(1)).save(any());

        assertNotNull(reservationArgumentCaptor.getValue());

        assertEquals(status, reservationArgumentCaptor.getValue().getStatus(), "The status should be PAID but it is not.");
    }

}
