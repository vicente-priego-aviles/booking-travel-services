package com.mercedesbenz.carservice.service.impl;

import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.basedomains.dto.cars.CarReservationFiltersDto;
import com.mercedesbenz.carservice.entity.Availability;
import com.mercedesbenz.carservice.entity.Car;
import com.mercedesbenz.carservice.entity.Reservation;
import com.mercedesbenz.carservice.helpers.BookingAvailabilityHelper;
import com.mercedesbenz.carservice.helpers.dto.BookingAvailabilityDto;
import com.mercedesbenz.carservice.repository.AvailabilityRepository;
import com.mercedesbenz.carservice.repository.CarRepository;
import com.mercedesbenz.carservice.repository.ReservationRepository;
import com.mercedesbenz.carservice.service.APIClient;
import com.mercedesbenz.carservice.stream.ReservationProducer;
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

import java.util.*;

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
    private AvailabilityRepository availabilityRepository;

    @Mock
    private ReservationProducer reservationProducer;

    @Mock
    private BookingAvailabilityHelper bookingAvailabilityHelper;

    @InjectMocks
    private CarServiceImpl carService;

    @Captor
    ArgumentCaptor<Availability> availabilityArgumentCaptor;

    @Captor
    ArgumentCaptor<Reservation> reservationArgumentCaptor;

    Car car;
    Reservation reservation;
    Reservation reservationToCancel;

    @BeforeEach
    void setUp() {
        Availability availability = new Availability();
        availability.setStartDate(1672570800000L); // 01/01/2023 12:00:00 CET
        availability.setEndDate(1675155600000L); // 31/01/2023 10:00:00 CET
        Availability availability2 = new Availability();
        availability2.setStartDate(1676458800000L); // 15/02/2023 12:00:00 CET
        availability2.setEndDate(1677322800000L); // 25/02/2023 10:00:00 CET
        List<Availability> availabilities = new ArrayList<>();
        availabilities.add(availability);
        availabilities.add(availability2);
        car = new Car();
        car.setBrand("Mercedes-Benz");
        car.setModel("Clase A");
        car.setLicense("0000 AAA");
        car.setCostPerDay(5999L);
        car.setAvailabilities(availabilities);

        reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setCar(car);
        reservation.setStatus(Status.IN_PROGRESS);
        reservation.setStartDate(1672570800000L);
        reservation.setEndDate(1675155600000L);

        reservationToCancel = new Reservation();
        reservationToCancel.setId(UUID.randomUUID());
        reservationToCancel.setCar(car);
        reservationToCancel.setStatus(Status.IN_PROGRESS);
        reservationToCancel.setStartDate(1000000000000L);
        reservationToCancel.setEndDate(1500000000000L);
    }

    @Test
    void bookCarReservationAtTheStartOfAvailability() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        carReservationFiltersDto.setStartDate(1672570800000L); // 01/01/2023 12:00:00 CET
        carReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id);

        BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
        bookingAvailabilityDto.setAvailabilityBeforeReservation(null);
        bookingAvailabilityDto.setAvailabilityAfterReservation(new Availability(null, 1676026800000L, 1675155600000L, car));
        bookingAvailabilityDto.setAvailabilityBookable(new Availability(null, 1672570800000L, 1673341200000L, car));
        List<Availability> availabilitiesToSaveWithCar = new ArrayList<>();
        availabilitiesToSaveWithCar.add(new Availability(null, 1676458800000L, 1677322800000L, car));
        bookingAvailabilityDto.setAvailabilitiesToSaveWithCar(availabilitiesToSaveWithCar);

        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        doNothing().when(reservationProducer).send(any());
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);
        when(bookingAvailabilityHelper.calculateAvailabilities(any(), any())).thenReturn(bookingAvailabilityDto);

        carService.bookCar(car.getId(), carReservationFiltersDto);

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        assertEquals(carReservationFiltersDto.getReservationID(), reservationArgumentCaptor.getValue().getId(), "Reservation ID saved is not correct");
        assertEquals(carReservationFiltersDto.getStartDate(), reservationArgumentCaptor.getValue().getStartDate(), "Reservation startDate is not correct");
        assertEquals(carReservationFiltersDto.getEndDate(), reservationArgumentCaptor.getValue().getEndDate(), "Reservation endDate is not correct");
        assertEquals(Status.IN_PROGRESS, reservationArgumentCaptor.getValue().getStatus(), "Reservation status should be IN_PROGRESS");
    }

    @Test
    void bookRoomReservationInTheMiddleOfAvailability() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        carReservationFiltersDto.setStartDate(1672916400000L); // 05/01/2023 12:00:00 CET
        carReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id);

        BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
        bookingAvailabilityDto.setAvailabilityBeforeReservation(new Availability(null, 1672570800000L, 1672909200000L, car));
        bookingAvailabilityDto.setAvailabilityAfterReservation(new Availability(null, 1673348400000L, 1675069200000L, car));
        bookingAvailabilityDto.setAvailabilityBookable(new Availability(null, 1672916400000L, 1673341200000L, car));
        List<Availability> availabilitiesToSaveWithCar = new ArrayList<>();
        availabilitiesToSaveWithCar.add(new Availability(null, 1676458800000L, 1677322800000L, car));
        bookingAvailabilityDto.setAvailabilitiesToSaveWithCar(availabilitiesToSaveWithCar);

        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        doNothing().when(reservationProducer).send(any());
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);
        when(bookingAvailabilityHelper.calculateAvailabilities(any(), any())).thenReturn(bookingAvailabilityDto);

        carService.bookCar(car.getId(), carReservationFiltersDto);

        verify(availabilityRepository, times(2)).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getAllValues());
        assertNotNull(reservationArgumentCaptor.getValue());

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID((any()));

        assertEquals(carReservationFiltersDto.getReservationID(), reservationArgumentCaptor.getValue().getId(), "Reservation ID is not correct");
        assertEquals(carReservationFiltersDto.getStartDate(), reservationArgumentCaptor.getValue().getStartDate(), "Reservation startDate is not correct");
        assertEquals(carReservationFiltersDto.getEndDate(), reservationArgumentCaptor.getValue().getEndDate(), "Reservation endDate is not correct");
        assertEquals(Status.IN_PROGRESS, reservationArgumentCaptor.getValue().getStatus(), "Reservation status should be IN_PROGRESS");
    }

    @Test
    void bookRoomReservationAtTheEndOfAvailability() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        carReservationFiltersDto.setStartDate(1673348400000L); // 10/01/2023 12:00:00 CET
        carReservationFiltersDto.setEndDate(1675155600000L); // 31/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id);

        BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
        bookingAvailabilityDto.setAvailabilityBeforeReservation(new Availability(null, 1672570800000L, 1673341200000L, car));
        bookingAvailabilityDto.setAvailabilityAfterReservation(null);
        bookingAvailabilityDto.setAvailabilityBookable(new Availability(null, 1673348400000L, 1675155600000L, car));
        List<Availability> availabilitiesToSaveWithCar = new ArrayList<>();
        availabilitiesToSaveWithCar.add(new Availability(null, 1676458800000L, 1677322800000L, car));
        bookingAvailabilityDto.setAvailabilitiesToSaveWithCar(availabilitiesToSaveWithCar);

        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        doNothing().when(reservationProducer).send(any());
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);
        when(bookingAvailabilityHelper.calculateAvailabilities(any(), any())).thenReturn(bookingAvailabilityDto);

        carService.bookCar(car.getId(), carReservationFiltersDto);

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        assertEquals(carReservationFiltersDto.getReservationID(), reservationArgumentCaptor.getValue().getId(), "Reservation ID saved is not correct");
        assertEquals(carReservationFiltersDto.getStartDate(), reservationArgumentCaptor.getValue().getStartDate(), "Reservation startDate is not correct");
        assertEquals(carReservationFiltersDto.getEndDate(), reservationArgumentCaptor.getValue().getEndDate(), "Reservation endDate is not correct");
        assertEquals(Status.IN_PROGRESS, reservationArgumentCaptor.getValue().getStatus(), "Reservation status should be IN_PROGRESS");
    }

    @Test
    void cancelReservation() {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservationToCancel));
        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));

        carService.cancelReservation(UUID.randomUUID());

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        verify(availabilityRepository, times(1)).save(any());
        verify(carRepository, times(1)).save(any());
        verify(reservationRepository, times(1)).save(any());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        assertEquals(1000000000000L, availabilityArgumentCaptor.getValue().getStartDate(), "The startDate is not matching.");
        assertEquals(1500000000000L, availabilityArgumentCaptor.getValue().getEndDate(), "The endDate is not matching.");
        assertEquals(Status.CANCELLED, reservationArgumentCaptor.getValue().getStatus(), "The status should be CANCELLED and it is not.");
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    void updateReservationStatus(Status status) {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservation));

        carService.updateReservationStatus(UUID.randomUUID(), status);

        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        verify(reservationRepository, times(1)).save(any());

        assertNotNull(reservationArgumentCaptor.getValue());

        assertEquals(status, reservationArgumentCaptor.getValue().getStatus(), "The status should be PAID but it is not.");
    }

}
