package com.company.basedomains.dto.hotel;

import lombok.Data;

import java.util.UUID;

@Data
public class ReservationDto {
    private String id;
    private RoomDto room;
    private Long startDate;
    private Long endDate;
    private String status;
}
