package com.mercedesbenz.basedomains.dto;

import java.util.List;
import java.util.UUID;

public class HotelDto {
    private UUID id;
    private String name;
    private String direction;
    private Long costPerNight;

    private List<RoomDto> rooms;

}
