package com.mercedesbenz.basedomains.dto;

import com.mercedesbenz.basedomains.dto.Status;
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
    private Boolean flightBooked;
    private Boolean hotelBooked;
    private Boolean carBooked;
}
