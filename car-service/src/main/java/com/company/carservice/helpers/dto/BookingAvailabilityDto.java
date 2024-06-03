package com.company.carservice.helpers.dto;

import com.company.carservice.entity.Availability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingAvailabilityDto {
    private Availability availabilityBookable;
    private Availability availabilityBeforeReservation;
    private Availability availabilityAfterReservation;
    private List<Availability> availabilitiesToSaveWithCar;
}
