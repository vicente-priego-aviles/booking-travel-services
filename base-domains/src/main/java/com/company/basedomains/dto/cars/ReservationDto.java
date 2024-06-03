package com.company.basedomains.dto.cars;

import lombok.Data;

import java.util.UUID;

@Data
public class ReservationDto {
    private String id;
    private CarDto car;
    private Long startDate;
    private Long endDate;
    private String status;
}
