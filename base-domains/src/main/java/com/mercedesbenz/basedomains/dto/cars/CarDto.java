package com.mercedesbenz.basedomains.dto.cars;

import com.mercedesbenz.basedomains.dto.AvailabilityDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CarDto {
    private UUID id;
    @NotEmpty (message = "Brand is required")
    private String brand;
    @NotEmpty(message = "Model is required")
    private String model;
    @NotEmpty(message = "License is required")
    private String license;
    @NotEmpty(message = "Cost per day is required")
    @Min(value = 0, message = "Cost per day can not be less than 0")
    private Long costPerDay;
    @NotEmpty(message = "Availabilities is required")
    private List<AvailabilityDto> availabilities;
}
