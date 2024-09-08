package com.company.basedomains.dto.cars;

import lombok.Data;

import java.util.UUID;

@Data
public class ReservationDto {
    private String id;
    private CarDto car;
    private String status;
}
