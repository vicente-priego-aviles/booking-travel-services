package com.mercedesbenz.basedomains.constraints;

import com.mercedesbenz.basedomains.constraints.validator.ValidReservationEndDateValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Constraint(validatedBy = ValidReservationEndDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidReservationEndDate {
    String message() default "End date must be or before 10:00:00 am";
}
