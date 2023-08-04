package com.mercedesbenz.basedomains.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;

@Data
public class AvailabilityDto {
    @NotNull(message = "Start date is required")
    private Long startDate;
    @NotNull(message = "End date is required")
    private Long endDate;
}
