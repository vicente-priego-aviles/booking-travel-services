package com.mercedesbenz.basedomains.constraints.validator;

import com.mercedesbenz.basedomains.constraints.ValidReservationStartDate;
import com.mercedesbenz.basedomains.dto.hotel.RoomReservationFiltersDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Calendar;

public class ValidReservationStartDateValidator implements ConstraintValidator<ValidReservationStartDate, Long> {
    @Override
    public boolean isValid(Long startDate, ConstraintValidatorContext constraintValidatorContext) {
        if (startDate == null) return false;

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(startDate);
        Calendar minTime = Calendar.getInstance();
        minTime.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DATE), 12, 0, 0);

        return (minTime.getTimeInMillis() == startDate || time.after(minTime));
    }
}
