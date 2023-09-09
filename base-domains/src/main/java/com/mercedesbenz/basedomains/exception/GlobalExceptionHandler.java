package com.mercedesbenz.basedomains.exception;

import com.mercedesbenz.basedomains.dto.ErrorDto;
import com.mercedesbenz.basedomains.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.List;

@RestControllerAdvice
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

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ResponseDto> handlePaymentException(PaymentException exception, WebRequest webRequest) {
        ErrorDto error = new ErrorDto(exception.getResourceName(), exception.getMessage(), (new Date()).getTime(), webRequest.getDescription(false));
        ResponseDto response = new ResponseDto(error, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, WebRequest webRequest) {
        StringBuilder concatenatedMessages = new StringBuilder();
        List<ObjectError> errorList = exception.getBindingResult().getAllErrors();
        for (ObjectError objectError : errorList) {
            concatenatedMessages.append(objectError.getDefaultMessage());
            concatenatedMessages.append(". ");
        }

        ErrorDto error = new ErrorDto(exception.getClass().toString(), concatenatedMessages.toString().trim(), (new Date()).getTime(), webRequest.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseDto> handleServiceException(ServiceException exception, WebRequest webRequest) {
        ErrorDto error = new ErrorDto(exception.getResourceName(), exception.getMessage(), (new Date()).getTime(), webRequest.getDescription(false));
        ResponseDto response = new ResponseDto(error, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
