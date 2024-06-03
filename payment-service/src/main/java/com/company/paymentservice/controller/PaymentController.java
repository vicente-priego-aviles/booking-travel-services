package com.company.paymentservice.controller;

import com.company.basedomains.dto.ResponseDto;
import com.company.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "API REST for Payments",
        description = "API to pay for your travel reservations"
)
@RestController
@RequestMapping("api/payments")
@AllArgsConstructor
public class PaymentController {

    private PaymentService paymentService;

    @Operation(
            summary = "Get all payment reservations",
            description = "Get all the payment reservations with the status of the flight, hotel and car reservation."
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping
    public ResponseEntity<ResponseDto> getAll() {
        ResponseDto response = new ResponseDto(null, paymentService.getAllReservations());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Pay Reservation ID",
            description = "Start payment of the reservation with identifier ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @PostMapping("/{id}")
    public ResponseEntity<ResponseDto> pay(@PathVariable String id) {
        ResponseDto response = new ResponseDto(null, paymentService.payReservation(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
