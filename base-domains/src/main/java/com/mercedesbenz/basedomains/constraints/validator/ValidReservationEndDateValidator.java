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
        Calendar maxTime = Calendar.getInstance();
        maxTime.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DATE), 10, 0, 0);

        return (time.before(maxTime) || maxTime.getTimeInMillis() == endDate);
    }
}
