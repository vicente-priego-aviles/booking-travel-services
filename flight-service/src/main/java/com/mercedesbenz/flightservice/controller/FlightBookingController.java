package com.mercedesbenz.flightservice.controller;

import com.mercedesbenz.basedomains.dto.FlightDto;
import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.flightservice.service.FlightService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights-booking")
@AllArgsConstructor
public class FlightBookingController {

    private FlightService flightService;

    @PostMapping
    public ResponseEntity<ResponseDto> insertCollectionOfFlights(@RequestBody List<FlightDto> flights) {
        ResponseDto response = new ResponseDto(null, flightService.insertAll(flights));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getAllFlights() {
        ResponseDto response = new ResponseDto(null, flightService.findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getFlightById(@PathVariable UUID id) {
        ResponseDto response = new ResponseDto(null, flightService.findOne(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
