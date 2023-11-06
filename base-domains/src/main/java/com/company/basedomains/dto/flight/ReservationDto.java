package com.company.basedomains.dto.flight;

import lombok.Data;

import java.util.UUID;

@Data
public class ReservationDto {
    private UUID id;
    private FlightDto flight;
    private String status;
}
