package com.company.basedomains.constraints;

import com.company.basedomains.constraints.validator.ValidReservationStartDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = ValidReservationStartDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidReservationStartDate {
    String message() default "Start date must be at 12:00:00 pm";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
