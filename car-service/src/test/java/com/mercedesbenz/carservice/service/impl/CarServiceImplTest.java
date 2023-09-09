package com.mercedesbenz.carservice.service.impl;

import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.basedomains.dto.cars.CarReservationFiltersDto;
import com.mercedesbenz.carservice.entity.Availability;
import com.mercedesbenz.carservice.entity.Car;
import com.mercedesbenz.carservice.entity.Reservation;
import com.mercedesbenz.carservice.repository.AvailabilityRepository;
import com.mercedesbenz.carservice.repository.CarRepository;
import com.mercedesbenz.carservice.repository.ReservationRepository;
import com.mercedesbenz.carservice.service.APIClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private CarServiceImpl carService;

    @Captor
    ArgumentCaptor<Availability> availabilityArgumentCaptor;

    @Captor
    ArgumentCaptor<Reservation> reservationArgumentCaptor;

    Car car;

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
    }

    @Test
    void bookCarReservationAtTheStartOfAvailability() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        carReservationFiltersDto.setStartDate(1672570800000L); // 01/01/2023 12:00:00 CET
        carReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id);

        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);

        carService.bookCar(car.getId(), carReservationFiltersDto);

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        dateCheck.set(2023, Calendar.JANUARY, 10, 12, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 31, 10, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 10, 10, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of reservation is not correct");
    }

    @Test
    void bookRoomReservationInTheMiddleOfAvailability() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        carReservationFiltersDto.setStartDate(1672916400000L); // 05/01/2023 12:00:00 CET
        carReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id);

        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);

        carService.bookCar(car.getId(), carReservationFiltersDto);

        verify(availabilityRepository, times(2)).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getAllValues());
        assertNotNull(availabilityArgumentCaptor.getAllValues().get(0));
        assertNotNull(availabilityArgumentCaptor.getAllValues().get(1));
        assertNotNull(reservationArgumentCaptor.getValue());

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID((any()));

        dateCheck.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getAllValues().get(0).getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 5, 10, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getAllValues().get(0).getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 10, 12, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getAllValues().get(1).getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 31, 10, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getAllValues().get(1).getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 5, 12, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 10, 10, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));
    }

    @Test
    void bookRoomReservationAtTheEndOfAvailability() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        carReservationFiltersDto.setStartDate(1673348400000L); // 10/01/2023 12:00:00 CET
        carReservationFiltersDto.setEndDate(1675155600000L); // 31/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id);

        when(carRepository.findById(any())).thenReturn(Optional.ofNullable(car));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);

        carService.bookCar(car.getId(), carReservationFiltersDto);

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        dateCheck.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 10, 10, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 10, 12, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 31, 10, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of reservation is not correct");
    }
}
