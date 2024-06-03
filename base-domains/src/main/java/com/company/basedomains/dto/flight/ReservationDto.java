package com.company.basedomains.dto.flight;

import lombok.Data;

import java.util.UUID;

@Data
public class ReservationDto {
    private String id;
    private FlightDto flight;
    private String status;
}
