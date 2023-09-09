package com.mercedesbenz.basedomains.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PaymentException extends BookingTravelException {

    public PaymentException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not able to pay due to missing element with %s : %s", resourceName, fieldName, fieldValue), resourceName, fieldName, fieldValue);
    }
}
