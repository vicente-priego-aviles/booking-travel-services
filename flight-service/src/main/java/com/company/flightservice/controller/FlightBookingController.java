package com.company.flightservice.controller;

import com.company.basedomains.dto.flight.FlightDto;
import com.company.basedomains.dto.ResponseDto;
import com.company.flightservice.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "API REST for Flight Booking",
        description = "API to insert possible flights bookable, get them and book a flight"
)
@RestController
@RequestMapping("api/flights-booking")
@AllArgsConstructor
public class FlightBookingController {

    private FlightService flightService;

    @Operation(
            summary = "Insert a collection of Flights",
            description = "Insert a collection of flights with the corresponding seats"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED",
            content = @Content(
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "Success response object after inserting list of Flights.",
                                    value = "{\n" +
                                            "\t\"error\": null,\n" +
                                            "\t\"data\": [\n" +
                                            "\t\t\t{\"id\": \"0000-000-000\", \n\t\t\t\"reference\": \"00MB\", \n\t\t\t\"airline\": \"Lufthansa\", \n\t\t\t\"departureAirportCode\": \"MAD\", \n\t\t\t\"departureAirportName\": \"Madrid\", \n\t\t\t\"departureTime\": 1000000000, \n\t\t\t\"arrivalTime\": 200000000, \n\t\t\t\"arrivalAirportCode\": \"STU\", \n\t\t\t\"arrivalAirportName\": \"Stuttgart\", \n\t\t\t\"cost\": 10, \n\t\t\t\"remainingSeats\", 4}, \n" +
                                            "\t\t\t{\"id\": \"0000-000-000\", \n\t\t\t\"reference\": \"00MB\", \n\t\t\t\"airline\": \"Lufthansa\", \n\t\t\t\"departureAirportCode\": \"MAD\", \n\t\t\t\"departureAirportName\": \"Madrid\", \n\t\t\t\"departureTime\": 1000000000, \n\t\t\t\"arrivalTime\": 200000000, \n\t\t\t\"arrivalAirportCode\": \"STU\", \n\t\t\t\"arrivalAirportName\": \"Stuttgart\", \n\t\t\t\"cost\": 10, \n\t\t\t\"remainingSeats\", 4}\n\t]\n}"
                            )
                    }
            )
    )
    @PostMapping
    public ResponseEntity<ResponseDto> insertCollectionOfFlights(@RequestBody @Valid List<FlightDto> flights) {
        ResponseDto response = new ResponseDto(null, flightService.insertAll(flights));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get All Flights",
            description = "Get All the available flights to book"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping
    public ResponseEntity<ResponseDto> getAllFlightsAvailable() {
        ResponseDto response = new ResponseDto(null, flightService.findAllAvailable());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Flight by ID",
            description = "Get one Flight by the ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getFlightById(@PathVariable String id) {
        ResponseDto response = new ResponseDto(null, flightService.findOne(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get All Bookings",
            description = "Get all the bookings with the corresponding status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/book")
    public ResponseEntity<ResponseDto> getAllBookings() {
        ResponseDto response = new ResponseDto(null, flightService.getAllBookings());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Book a flight by ID",
            description = "Book a flight by ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping("/book/{flightId}")
    public ResponseEntity<ResponseDto> bookFlight(@PathVariable String flightId){
        ResponseDto response = new ResponseDto(null, flightService.bookFlight(flightId));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get reservation by ID",
            description = "Check if a reservation ID exists"
    )
    @GetMapping("/book/check/{reservationID}")
    public ResponseEntity<ResponseDto> checkReservationID(@PathVariable String reservationID) {
        ResponseDto response = new ResponseDto(null, flightService.checkReservation(reservationID));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
