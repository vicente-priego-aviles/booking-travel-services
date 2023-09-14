package com.mercedesbenz.carservice.controller;

import com.mercedesbenz.basedomains.dto.cars.CarDto;
import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.basedomains.dto.cars.CarReservationFiltersDto;
import com.mercedesbenz.carservice.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
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
        name = "API REST for Car Booking",
        description = "API to insert possible cars bookable, get them and book a car"
)
@RestController
@RequestMapping("api/cars-booking")
@AllArgsConstructor
public class CarBookingController {

    private CarService carService;

    @Operation(
            summary = "Insert a collection of Cars",
            description = "Insert a collection of cars with their availability"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping
    public ResponseEntity<ResponseDto> insertCollectionOfCars(@RequestBody @Valid List<CarDto> cars) {
        ResponseDto response = new ResponseDto(null, carService.insertAll(cars));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get All Cars",
            description = "Get All the available car and their availability"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping
    public ResponseEntity<ResponseDto> getAllCars() {
        ResponseDto response = new ResponseDto(null, carService.findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Car by ID",
            description = "Get one Car by the ID its availability"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getCarById(@PathVariable UUID id) {
        ResponseDto response = new ResponseDto(null, carService.findOne(id));
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
        ResponseDto response = new ResponseDto(null, carService.getAllBookings());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Book a car by ID",
            description = "Book a car by ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping("/book/{carId}")
    public ResponseEntity<ResponseDto> bookCar(@PathVariable UUID carId, @RequestBody @Valid CarReservationFiltersDto carReservationFiltersDto) {
        ResponseDto response = new ResponseDto(null, carService.bookCar(carId, carReservationFiltersDto));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
