package com.mercedesbenz.basedomains.dto.flight;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
public class FlightDto {
    private UUID id;
    @NotEmpty(message = "Reference is required")
    private String reference;
    @NotEmpty(message = "Airline is required")
    private String airline;
    @NotEmpty(message = "Departure Airport Code is required")
    private String departureAirportCode;
    @NotEmpty(message = "Departure Airport Name is required")
    private String departureAirportName;
    @NotEmpty(message = "Departure Time is required")
    private Long departureTime;
    @NotEmpty(message = "Arrival Airport Code is required")
    private String arrivalAirportCode;
    @NotEmpty(message = "Arrival Airport Name is required")
    private String arrivalAirportName;
    @NotEmpty(message = "Arrival Time is required")
    private Long arrivalTime;
    @NotEmpty(message = "Cost is required")
    @Min(value = 0, message = "Cost can not be less than 0")
    private Long cost;
    private Long remainingSeats;
}
