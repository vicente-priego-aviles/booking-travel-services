package com.company.basedomains.dto.hotel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class HotelDto {
    private String id;
    @NotEmpty(message = "Name is required")
    private String name;
    @NotEmpty(message = "Direction is required")
    private String direction;
    @NotEmpty(message = "Cost Per Night is required")
    @Min(value = 0, message = "Cost Per Night can not be less than 0")
    private Long costPerNight;
    @NotEmpty(message = "RemainingRooms is required")
    @Min(value = 1, message = "Remaining Rooms can not be less than 1")
    private Long remainingRooms;

}
