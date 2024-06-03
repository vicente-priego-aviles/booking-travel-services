package com.company.carservice.helpers;

import com.company.basedomains.dto.cars.CarReservationFiltersDto;
import com.company.carservice.entity.Availability;
import com.company.carservice.entity.Car;
import com.company.carservice.helpers.BookingAvailabilityHelper;
import com.company.carservice.helpers.dto.BookingAvailabilityDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class BookingAvailabilityHelperTest {

    @InjectMocks
    private BookingAvailabilityHelper bookingAvailabilityHelper;

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
        car.setId(UUID.randomUUID().toString());
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
        carReservationFiltersDto.setReservationID(id.toString());

        BookingAvailabilityDto bookingAvailabilityDto = bookingAvailabilityHelper.calculateAvailabilities(car, carReservationFiltersDto);

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        dateCheck.set(2023, Calendar.JANUARY, 10, 12, 0, 0);
        argumentDate.setTimeInMillis(bookingAvailabilityDto.getAvailabilityAfterReservation().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 31, 10, 0, 0);
        argumentDate.setTimeInMillis(bookingAvailabilityDto.getAvailabilityAfterReservation().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of availability before reservation is not correct");

        assertNull(bookingAvailabilityDto.getAvailabilityBeforeReservation(), "AvailabilityBeforeReservation should be null and it is not");
        assertEquals(1, bookingAvailabilityDto.getAvailabilitiesToSaveWithCar().size(), "AvailabilitiesToSaveWithCar should only have 1 element");
    }

    @Test
    void bookRoomReservationInTheMiddleOfAvailability() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        carReservationFiltersDto.setStartDate(1672916400000L); // 05/01/2023 12:00:00 CET
        carReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id.toString());

        BookingAvailabilityDto bookingAvailabilityDto = bookingAvailabilityHelper.calculateAvailabilities(car, carReservationFiltersDto);

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        dateCheck.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        argumentDate.setTimeInMillis(bookingAvailabilityDto.getAvailabilityBeforeReservation().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 5, 10, 0, 0);
        argumentDate.setTimeInMillis(bookingAvailabilityDto.getAvailabilityBeforeReservation().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 10, 12, 0, 0);
        argumentDate.setTimeInMillis(bookingAvailabilityDto.getAvailabilityAfterReservation().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 31, 10, 0, 0);
        argumentDate.setTimeInMillis(bookingAvailabilityDto.getAvailabilityAfterReservation().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        assertEquals(1, bookingAvailabilityDto.getAvailabilitiesToSaveWithCar().size(), "AvailabilitiesToSaveWithCar should only have 1 element");
    }

    @Test
    void bookRoomReservationAtTheEndOfAvailability() {
        CarReservationFiltersDto carReservationFiltersDto = new CarReservationFiltersDto();
        carReservationFiltersDto.setStartDate(1673348400000L); // 10/01/2023 12:00:00 CET
        carReservationFiltersDto.setEndDate(1675155600000L); // 31/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        carReservationFiltersDto.setReservationID(id.toString());

        BookingAvailabilityDto bookingAvailabilityDto = bookingAvailabilityHelper.calculateAvailabilities(car, carReservationFiltersDto);

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        dateCheck.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        argumentDate.setTimeInMillis(bookingAvailabilityDto.getAvailabilityBeforeReservation().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 10, 10, 0, 0);
        argumentDate.setTimeInMillis(bookingAvailabilityDto.getAvailabilityBeforeReservation().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of availability before reservation is not correct");

        assertNull(bookingAvailabilityDto.getAvailabilityAfterReservation(), "AvailabilityAfterReservation should be null and it is not");
        assertEquals(1, bookingAvailabilityDto.getAvailabilitiesToSaveWithCar().size(), "AvailabilitiesToSaveWithCar should only have 1 element");
    }

}
