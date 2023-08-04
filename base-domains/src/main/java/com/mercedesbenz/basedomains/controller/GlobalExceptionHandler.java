package com.mercedesbenz.basedomains.controller;

import com.mercedesbenz.basedomains.dto.ErrorDto;
import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.basedomains.exceptions.NotBookableException;
import com.mercedesbenz.basedomains.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {
        ErrorDto error = new ErrorDto(exception.getResourceName(), exception.getMessage(), (new Date()).getTime(), webRequest.getDescription(false));
        ResponseDto response = new ResponseDto(error, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotBookableException.class)
    public ResponseEntity<ResponseDto> handleNotBookableException(NotBookableException exception, WebRequest webRequest) {
        ErrorDto error = new ErrorDto(exception.getResourceName(), exception.getMessage(), (new Date()).getTime(), webRequest.getDescription(false));
        ResponseDto response = new ResponseDto(error, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
