package com.mercedesbenz.carservice.helpers;

import com.mercedesbenz.basedomains.dto.cars.CarReservationFiltersDto;
import com.mercedesbenz.basedomains.exception.NotBookableException;
import com.mercedesbenz.carservice.helpers.dto.BookingAvailabilityDto;
import com.mercedesbenz.carservice.entity.Availability;
import com.mercedesbenz.carservice.entity.Car;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class BookingAvailabilityHelper {
    public BookingAvailabilityDto calculateAvailabilities(Car car, CarReservationFiltersDto carReservationFiltersDto) {
        Availability availabilityBookable = null;

        Calendar dateNormalizator = Calendar.getInstance();

        dateNormalizator.setTimeInMillis(carReservationFiltersDto.getStartDate());
        dateNormalizator.set(dateNormalizator.get(Calendar.YEAR), dateNormalizator.get(Calendar.MONTH), dateNormalizator.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
        carReservationFiltersDto.setStartDate(dateNormalizator.getTimeInMillis());

        dateNormalizator.setTimeInMillis(carReservationFiltersDto.getEndDate());
        dateNormalizator.set(dateNormalizator.get(Calendar.YEAR), dateNormalizator.get(Calendar.MONTH), dateNormalizator.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
        carReservationFiltersDto.setEndDate(dateNormalizator.getTimeInMillis());

        if (car != null && car.getAvailabilities() != null && !car.getAvailabilities().isEmpty()) {
            List<Availability> availabilitiesToSaveWithCar = new ArrayList<>();
            for (Availability availability : car.getAvailabilities()) {
                if (availability.getStartDate() <= carReservationFiltersDto.getStartDate() && availability.getEndDate() >= carReservationFiltersDto.getEndDate()) {
                    availabilityBookable = availability;
                } else {
                    availabilitiesToSaveWithCar.add(availability);
                }
            }
            if (availabilityBookable != null &&
                    availabilityBookable.getStartDate() <= carReservationFiltersDto.getStartDate() &&
                    carReservationFiltersDto.getEndDate() <= availabilityBookable.getEndDate()) {
                Availability availabilityBeforeReservation = null;
                Availability availabilityAfterReservation = null;
                Calendar calendarHelper = Calendar.getInstance();

                Calendar availabilityBookableStartDate = Calendar.getInstance();
                Calendar availabilityBookableEndDate = Calendar.getInstance();
                Calendar carReservationFiltersDtoStartDate = Calendar.getInstance();
                Calendar carReservationFiltersDtoEndDate = Calendar.getInstance();
                availabilityBookableStartDate.setTimeInMillis(availabilityBookable.getStartDate());
                availabilityBookableEndDate.setTimeInMillis(availabilityBookable.getEndDate());
                carReservationFiltersDtoStartDate.setTimeInMillis(carReservationFiltersDto.getStartDate());
                carReservationFiltersDtoEndDate.setTimeInMillis(carReservationFiltersDto.getEndDate());

                if (availabilityBookableStartDate.get(Calendar.YEAR) == carReservationFiltersDtoStartDate.get(Calendar.YEAR) &&
                        availabilityBookableStartDate.get(Calendar.MONTH) == carReservationFiltersDtoStartDate.get(Calendar.MONTH) &&
                        availabilityBookableStartDate.get(Calendar.DAY_OF_MONTH) == carReservationFiltersDtoStartDate.get(Calendar.DAY_OF_MONTH) &&
                        availabilityBookableStartDate.get(Calendar.HOUR) == carReservationFiltersDtoStartDate.get(Calendar.HOUR) &&
                        availabilityBookableStartDate.get(Calendar.MINUTE) == carReservationFiltersDtoStartDate.get(Calendar.MINUTE)) {
                    availabilityAfterReservation = new Availability();
                    calendarHelper.setTimeInMillis(carReservationFiltersDto.getEndDate());
                    calendarHelper.set(calendarHelper.get(Calendar.YEAR), calendarHelper.get(Calendar.MONTH), calendarHelper.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
                    availabilityAfterReservation.setStartDate(calendarHelper.getTimeInMillis());
                    if (availabilityBookableEndDate.get(Calendar.YEAR) == carReservationFiltersDtoEndDate.get(Calendar.YEAR)  &&
                            availabilityBookableEndDate.get(Calendar.MONTH) == carReservationFiltersDtoEndDate.get(Calendar.MONTH) &&
                            availabilityBookableEndDate.get(Calendar.DAY_OF_MONTH) == carReservationFiltersDtoEndDate.get(Calendar.DAY_OF_MONTH) &&
                            availabilityBookableEndDate.get(Calendar.HOUR) == carReservationFiltersDtoEndDate.get(Calendar.HOUR) &&
                            availabilityBookableEndDate.get(Calendar.MINUTE) == carReservationFiltersDtoEndDate.get(Calendar.MINUTE)) {
                        availabilityAfterReservation = null;
                    } else if (carReservationFiltersDtoEndDate.getTimeInMillis() <= availabilityBookableEndDate.getTimeInMillis()) {
                        calendarHelper.setTimeInMillis(availabilityBookableEndDate.getTimeInMillis());
                        calendarHelper.set(calendarHelper.get(Calendar.YEAR), calendarHelper.get(Calendar.MONTH), calendarHelper.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
                        availabilityAfterReservation.setEndDate(calendarHelper.getTimeInMillis());
                    } else {
                        availabilityAfterReservation = null;
                    }
                } else {
                    availabilityBeforeReservation = new Availability();
                    availabilityBeforeReservation.setStartDate(availabilityBookable.getStartDate());
                    calendarHelper.setTimeInMillis(carReservationFiltersDto.getStartDate());
                    calendarHelper.set(calendarHelper.get(Calendar.YEAR), calendarHelper.get(Calendar.MONTH), calendarHelper.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
                    availabilityBeforeReservation.setEndDate(calendarHelper.getTimeInMillis());

                    if(carReservationFiltersDtoEndDate.getTimeInMillis() < availabilityBookableEndDate.getTimeInMillis()) {
                        availabilityAfterReservation = new Availability();
                        calendarHelper.setTimeInMillis(carReservationFiltersDto.getEndDate());
                        calendarHelper.set(calendarHelper.get(Calendar.YEAR), calendarHelper.get(Calendar.MONTH), calendarHelper.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
                        availabilityAfterReservation.setStartDate(calendarHelper.getTimeInMillis());
                        availabilityAfterReservation.setEndDate(availabilityBookable.getEndDate());
                    }
                }

                BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
                bookingAvailabilityDto.setAvailabilityBookable(availabilityBookable);
                bookingAvailabilityDto.setAvailabilityBeforeReservation(availabilityBeforeReservation);
                bookingAvailabilityDto.setAvailabilityAfterReservation(availabilityAfterReservation);
                bookingAvailabilityDto.setAvailabilitiesToSaveWithCar(availabilitiesToSaveWithCar);

                return bookingAvailabilityDto;
            } else {
                throw new NotBookableException("CAR", "id", car.getId().toString());
            }
        } else {
            throw new NotBookableException("CAR", "id", car.getId().toString());
        }
    }
}
