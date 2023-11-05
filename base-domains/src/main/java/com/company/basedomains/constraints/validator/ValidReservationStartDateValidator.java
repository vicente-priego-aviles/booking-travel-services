package com.company.basedomains.constraints.validator;

import com.company.basedomains.constraints.ValidReservationStartDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Calendar;

public class ValidReservationStartDateValidator implements ConstraintValidator<ValidReservationStartDate, Long> {
    @Override
    public boolean isValid(Long startDate, ConstraintValidatorContext constraintValidatorContext) {
        if (startDate == null) return false;

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(startDate);

        return (time.get(Calendar.HOUR_OF_DAY) == 12 || time.get(Calendar.MINUTE) == 0);
    }
}
