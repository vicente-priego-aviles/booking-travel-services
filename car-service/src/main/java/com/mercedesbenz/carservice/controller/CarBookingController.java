package com.mercedesbenz.carservice.controller;

import com.mercedesbenz.basedomains.dto.CarDto;
import com.mercedesbenz.carservice.service.CarService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/car-api")
@AllArgsConstructor
public class CarBookingController {

    private CarService carService;

    @PostMapping
    public ResponseEntity<List<CarDto>> insert(@RequestBody List<CarDto> cars) {
        return new ResponseEntity<>(carService.insertAll(cars), HttpStatus.OK);
    }
}
