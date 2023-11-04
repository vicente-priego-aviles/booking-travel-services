package com.mercedesbenz.hotelservice.helpers;

import com.mercedesbenz.basedomains.dto.hotel.RoomReservationFiltersDto;
import com.mercedesbenz.basedomains.exception.NotBookableException;
import com.mercedesbenz.hotelservice.entity.Availability;
import com.mercedesbenz.hotelservice.entity.Room;
import com.mercedesbenz.hotelservice.helpers.dto.BookingAvailabilityDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class BookingAvailabilityHelper {
    public BookingAvailabilityDto calculateAvailabilities(Room room, RoomReservationFiltersDto roomReservationFiltersDto) {
        Availability availabilityBookable = null;

        Calendar dateNormalizator = Calendar.getInstance();

        dateNormalizator.setTimeInMillis(roomReservationFiltersDto.getStartDate());
        dateNormalizator.set(dateNormalizator.get(Calendar.YEAR), dateNormalizator.get(Calendar.MONTH), dateNormalizator.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
        roomReservationFiltersDto.setStartDate(dateNormalizator.getTimeInMillis());

        dateNormalizator.setTimeInMillis(roomReservationFiltersDto.getEndDate());
        dateNormalizator.set(dateNormalizator.get(Calendar.YEAR), dateNormalizator.get(Calendar.MONTH), dateNormalizator.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
        roomReservationFiltersDto.setEndDate(dateNormalizator.getTimeInMillis());

        if (room != null && room.getAvailabilities() != null && !room.getAvailabilities().isEmpty()) {
            List<Availability> availabilitiesToSaveWithRoom = new ArrayList<>();
            for (Availability availability : room.getAvailabilities()) {
                if (availability.getStartDate() <= roomReservationFiltersDto.getStartDate() && availability.getEndDate() >= roomReservationFiltersDto.getEndDate()) {
                    availabilityBookable = availability;
                } else {
                    availabilitiesToSaveWithRoom.add(availability);
                }
            }
            if (availabilityBookable != null &&
                availabilityBookable.getStartDate() <= roomReservationFiltersDto.getStartDate() &&
                roomReservationFiltersDto.getEndDate() <= availabilityBookable.getEndDate()) {
                Availability availabilityBeforeReservation = null;
                Availability availabilityAfterReservation = null;
                Calendar calendarHelper = Calendar.getInstance();

                Calendar availabilityBookableStartDate = Calendar.getInstance();
                Calendar availabilityBookableEndDate = Calendar.getInstance();
                Calendar roomReservationFiltersDtoStartDate = Calendar.getInstance();
                Calendar roomReservationFiltersDtoEndDate = Calendar.getInstance();
                availabilityBookableStartDate.setTimeInMillis(availabilityBookable.getStartDate());
                availabilityBookableEndDate.setTimeInMillis(availabilityBookable.getEndDate());
                roomReservationFiltersDtoStartDate.setTimeInMillis(roomReservationFiltersDto.getStartDate());
                roomReservationFiltersDtoEndDate.setTimeInMillis(roomReservationFiltersDto.getEndDate());

                if (availabilityBookableStartDate.get(Calendar.YEAR) == roomReservationFiltersDtoStartDate.get(Calendar.YEAR)  &&
                        availabilityBookableStartDate.get(Calendar.MONTH) == roomReservationFiltersDtoStartDate.get(Calendar.MONTH) &&
                        availabilityBookableStartDate.get(Calendar.DAY_OF_MONTH) == roomReservationFiltersDtoStartDate.get(Calendar.DAY_OF_MONTH) &&
                        availabilityBookableStartDate.get(Calendar.HOUR) == roomReservationFiltersDtoStartDate.get(Calendar.HOUR) &&
                        availabilityBookableStartDate.get(Calendar.MINUTE) == roomReservationFiltersDtoStartDate.get(Calendar.MINUTE)) {
                    availabilityAfterReservation = new Availability();
                    calendarHelper.setTimeInMillis(roomReservationFiltersDto.getEndDate());
                    calendarHelper.set(calendarHelper.get(Calendar.YEAR), calendarHelper.get(Calendar.MONTH), calendarHelper.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
                    availabilityAfterReservation.setStartDate(calendarHelper.getTimeInMillis());
                    if (availabilityBookableEndDate.get(Calendar.YEAR) == roomReservationFiltersDtoEndDate.get(Calendar.YEAR)  &&
                            availabilityBookableEndDate.get(Calendar.MONTH) == roomReservationFiltersDtoEndDate.get(Calendar.MONTH) &&
                            availabilityBookableEndDate.get(Calendar.DAY_OF_MONTH) == roomReservationFiltersDtoEndDate.get(Calendar.DAY_OF_MONTH) &&
                            availabilityBookableEndDate.get(Calendar.HOUR) == roomReservationFiltersDtoEndDate.get(Calendar.HOUR) &&
                            availabilityBookableEndDate.get(Calendar.MINUTE) == roomReservationFiltersDtoEndDate.get(Calendar.MINUTE)) {
                        availabilityAfterReservation = null;
                    } else if (roomReservationFiltersDtoEndDate.getTimeInMillis() < availabilityBookableEndDate.getTimeInMillis()) {
                        calendarHelper.setTimeInMillis(availabilityBookableEndDate.getTimeInMillis());
                        calendarHelper.set(calendarHelper.get(Calendar.YEAR), calendarHelper.get(Calendar.MONTH), calendarHelper.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
                        availabilityAfterReservation.setEndDate(calendarHelper.getTimeInMillis());
                    } else {
                        availabilityAfterReservation = null;
                    }
                } else {
                    availabilityBeforeReservation = new Availability();
                    availabilityBeforeReservation.setStartDate(availabilityBookable.getStartDate());
                    calendarHelper.setTimeInMillis(roomReservationFiltersDto.getStartDate());
                    calendarHelper.set(calendarHelper.get(Calendar.YEAR), calendarHelper.get(Calendar.MONTH), calendarHelper.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
                    availabilityBeforeReservation.setEndDate(calendarHelper.getTimeInMillis());

                    if (roomReservationFiltersDtoEndDate.getTimeInMillis() < availabilityBookableEndDate.getTimeInMillis()) {
                        availabilityAfterReservation = new Availability();
                        calendarHelper.setTimeInMillis(roomReservationFiltersDto.getEndDate());
                        calendarHelper.set(calendarHelper.get(Calendar.YEAR), calendarHelper.get(Calendar.MONTH), calendarHelper.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
                        availabilityAfterReservation.setStartDate(calendarHelper.getTimeInMillis());
                        availabilityAfterReservation.setEndDate(availabilityBookable.getEndDate());
                    }
                }

                BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
                bookingAvailabilityDto.setAvailabilityBookable(availabilityBookable);
                bookingAvailabilityDto.setAvailabilityBeforeReservation(availabilityBeforeReservation);
                bookingAvailabilityDto.setAvailabilityAfterReservation(availabilityAfterReservation);
                bookingAvailabilityDto.setAvailabilitiesToSaveWithRoom(availabilitiesToSaveWithRoom);

                return bookingAvailabilityDto;
            } else {
                throw new NotBookableException("HOTEL", "id", room.getId().toString());
            }
        } else {
            throw new NotBookableException("HOTEL", "id", room.getId().toString());
        }
    }
}
