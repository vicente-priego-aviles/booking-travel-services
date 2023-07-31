package com.mercedesbenz.basedomains.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FlightDto {
    private UUID id;
    private String reference;
    private String airline;
    private String departureAirportCode;
    private String departureAirportName;
    private Long departureTime;
    private String arrivalAirportCode;
    private String arrivalAirportName;
    private Long arrivalTime;
    private Long cost;
}
