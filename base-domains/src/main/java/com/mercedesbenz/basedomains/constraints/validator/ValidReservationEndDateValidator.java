package com.mercedesbenz.basedomains.constraints.validator;

import com.mercedesbenz.basedomains.constraints.ValidReservationEndDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Calendar;

public class ValidReservationEndDateValidator implements ConstraintValidator<ValidReservationEndDate, Long> {
    @Override
    public boolean isValid(Long endDate, ConstraintValidatorContext constraintValidatorContext) {
        if (endDate == null) return false;

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(endDate);

        return (time.get(Calendar.HOUR_OF_DAY) == 10 && time.get(Calendar.MINUTE) == 0);
    }
}
