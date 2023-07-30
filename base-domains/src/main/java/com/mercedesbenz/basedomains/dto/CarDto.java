package com.mercedesbenz.basedomains.dto;

import lombok.Data;

@Data
public class CarDto {
    private Long id;
    private String brand;
    private String model;
    private String license;
    private Long costPerDay;
    private AvailabilityDto availability;
}
