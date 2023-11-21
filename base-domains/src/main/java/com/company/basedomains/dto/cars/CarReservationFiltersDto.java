package com.company.basedomains.dto.cars;

import com.company.basedomains.constraints.ValidReservationEndDate;
import com.company.basedomains.constraints.ValidReservationStartDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CarReservationFiltersDto {
    @Schema(description = "Reservation ID. Required. ID gotten after flight reservation.")
    @NotNull(message = "Reservation ID is required")
    private UUID reservationID;

    @Schema(description = "Start Date of reservation. Required. Start date must be at 12:00:00 pm UTC")
    @NotNull(message = "Start date is required")
    @ValidReservationStartDate
    private Long startDate;

    @Schema(description = "End Date of reservation. Required. End date must be 10:00:00 am UTC")
    @NotNull(message = "End date is required")
    @ValidReservationEndDate
    private Long endDate;
}
