package com.company.basedomains.dto.hotel;

import com.company.basedomains.dto.AvailabilityDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RoomDto {
    private UUID id;
    private String title;
    @NotEmpty(message = "People Capacity of Room is required")
    @Min(value = 1, message = "People Capacity must be at least 1")
    private Long peopleCapacity;
    @NotEmpty(message = "Availabilities is required")
    private List<AvailabilityDto> availabilities;
}
