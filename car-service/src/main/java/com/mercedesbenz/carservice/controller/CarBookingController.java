package com.mercedesbenz.carservice.controller;

import com.mercedesbenz.basedomains.dto.CarDto;
import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.carservice.service.CarService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cars-booking")
@AllArgsConstructor
public class CarBookingController {

    private CarService carService;

    @PostMapping
    public ResponseEntity<ResponseDto> insertCollectionOfCars(@RequestBody List<CarDto> cars) {
        ResponseDto response = new ResponseDto(null, carService.insertAll(cars));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getAllCars() {
        ResponseDto response = new ResponseDto(null, carService.findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getCarById(@PathVariable UUID id) {
        ResponseDto response = new ResponseDto(null, carService.findOne(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
