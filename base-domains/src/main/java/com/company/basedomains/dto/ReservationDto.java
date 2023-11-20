package com.company.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    private UUID id;
    private Status status;
    private boolean flightBooked;
    private boolean hotelBooked;
    private boolean carBooked;
}
