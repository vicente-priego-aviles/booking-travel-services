package com.mercedesbenz.basedomains.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingTravelException extends RuntimeException {
    private String resourceName;
    private String fieldName;
    private String fieldValue;

    public BookingTravelException(String message, String resourceName, String fieldName, String fieldValue) {
        super(message);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
