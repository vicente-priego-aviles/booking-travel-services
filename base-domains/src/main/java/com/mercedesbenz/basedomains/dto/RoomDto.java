package com.mercedesbenz.basedomains.dto;

import java.util.List;
import java.util.UUID;

public class RoomDto {
    private UUID id;
    private String title;
    private String peopleCapacity;
    private List<AvailabilityDto> availabilities;
}
