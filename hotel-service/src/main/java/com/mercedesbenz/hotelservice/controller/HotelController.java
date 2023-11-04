package com.mercedesbenz.hotelservice.controller;

import com.mercedesbenz.basedomains.dto.hotel.RoomReservationFiltersDto;
import com.mercedesbenz.basedomains.dto.hotel.HotelDto;
import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.hotelservice.service.APIClient;
import com.mercedesbenz.hotelservice.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "API REST for Hotel Booking",
        description = "API to insert possible hotel/rooms bookable, get them and book a room in a hotel"
)
@RestController
@RequestMapping("api/hotels-booking")
@AllArgsConstructor
public class HotelController {
    private HotelService hotelService;

    @Operation(
            summary = "Insert a collection of Hotels",
            description = "Insert a collection of hotels with the corresponding rooms and their availability"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping
    public ResponseEntity<ResponseDto> insertCollectionOfHotels(@RequestBody @Valid List<HotelDto> hotels) {
        ResponseDto response = new ResponseDto(null, hotelService.insertAll(hotels));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get All Hotels",
            description = "Get All the available hotels and their rooms with their availability"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping
    public ResponseEntity<ResponseDto> getAllHotels() {
        ResponseDto response = new ResponseDto(null, hotelService.findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Hotel by ID",
            description = "Get one Hotel by the ID and its rooms with their availability"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getHotelById(@PathVariable UUID id) {
        ResponseDto response = new ResponseDto(null, hotelService.findOne(id));
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
        ResponseDto response = new ResponseDto(null, hotelService.getAllBookings());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Book a room in a Hotel by the room ID",
            description = "Book a room by the unique ROOM ID. As restrictions, startDate has to be at least dd/mm/yyyy 12:00:00 pm and endDate has to be at most dd/mm/yyyy 10:00:00 am"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping("/book/{roomId}")
    public ResponseEntity<ResponseDto> bookRoom(@PathVariable UUID roomId, @RequestBody @Valid RoomReservationFiltersDto roomReservationFiltersDto) {
        ResponseDto response = new ResponseDto(null, hotelService.bookRoom(roomId, roomReservationFiltersDto));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
