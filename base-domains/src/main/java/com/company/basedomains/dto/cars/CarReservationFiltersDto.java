package com.company.basedomains.dto.cars;

import com.company.basedomains.constraints.ValidReservationEndDate;
import com.company.basedomains.constraints.ValidReservationStartDate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CarReservationFiltersDto {
    @NotNull(message = "Reservation ID is required")
    private UUID reservationID;

    @NotNull(message = "Start date is required")
    @ValidReservationStartDate
    private Long startDate;

    @NotNull(message = "End date is required")
    @ValidReservationEndDate
    private Long endDate;
}
