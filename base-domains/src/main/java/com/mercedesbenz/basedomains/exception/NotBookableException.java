package com.mercedesbenz.basedomains.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotBookableException extends BookingTravelException {
    public NotBookableException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s is not currently bookable with %s : %s", resourceName, fieldName, fieldValue), resourceName, fieldName, fieldValue);
    }
}