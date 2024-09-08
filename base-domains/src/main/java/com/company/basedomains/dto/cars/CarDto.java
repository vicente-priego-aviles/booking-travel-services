package com.company.basedomains.dto.cars;

import com.company.basedomains.dto.AvailabilityDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CarDto {
    private String id;
    @NotEmpty (message = "Brand is required")
    private String brand;
    @NotEmpty(message = "Model is required")
    private String model;
    @NotEmpty(message = "Cost per day is required")
    @Min(value = 0, message = "Cost per day can not be less than 0")
    private Long costPerDay;
    @NotEmpty(message = "Remaining cars is required")
    @Min(value = 1, message = "Remaining cars can not be less than 1")
    private Long remainingCars;
}
