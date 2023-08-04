package com.mercedesbenz.basedomains.constraints;

import com.mercedesbenz.basedomains.constraints.validator.ValidReservationStartDateValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Constraint(validatedBy = ValidReservationStartDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidReservationStartDate {
    String message() default "Start date must be or after 12:00:00 pm";

}
