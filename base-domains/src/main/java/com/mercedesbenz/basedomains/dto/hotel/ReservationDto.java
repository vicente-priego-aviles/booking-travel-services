package com.mercedesbenz.basedomains.dto.hotel;

import lombok.Data;

@Data
public class ReservationDto {
    private String id;
    private RoomDto room;
    private Long startDate;
    private Long endDate;
    private String status;
}
