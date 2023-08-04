package com.mercedesbenz.basedomains.dto.hotel;

import lombok.Data;

@Data
public class ReservationDto {
    private RoomDto room;
    private Long startDate;
    private Long endDate;
}
