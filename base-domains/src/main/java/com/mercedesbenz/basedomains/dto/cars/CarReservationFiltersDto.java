package com.mercedesbenz.basedomains.dto.cars;

import com.mercedesbenz.basedomains.constraints.ValidReservationEndDate;
import com.mercedesbenz.basedomains.constraints.ValidReservationStartDate;
import jakarta.validation.constraints.NotEmpty;
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
