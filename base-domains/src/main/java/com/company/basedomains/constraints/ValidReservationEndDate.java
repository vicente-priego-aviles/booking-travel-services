package com.company.basedomains.constraints;

import com.company.basedomains.constraints.validator.ValidReservationEndDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = ValidReservationEndDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidReservationEndDate {
    String message() default "End date must be 10:00:00 am";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
