package com.company.basedomains.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Calendar;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceException extends BookingTravelException {
    public ServiceException(String serviceName) {
        super(String.format("%s is not available at %s", serviceName, Calendar.getInstance().getTimeInMillis()), serviceName, null, null);
    }
}