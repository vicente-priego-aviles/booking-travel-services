package com.company.basedomains.dto.hotel;

import com.company.basedomains.constraints.ValidReservationEndDate;
import com.company.basedomains.constraints.ValidReservationStartDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RoomReservationFiltersDto {

    @Schema(description = "Reservation ID. Required. ID gotten after flight reservation.")
    @NotNull(message = "reservation ID is required")
    private String reservationID;

    @Schema(description = "Start Date of reservation. Required. Start date must be at 12:00:00 pm UTC")
    @ValidReservationStartDate
    @NotNull(message = "Start Date is required")
    private Long startDate;

    @Schema(description = "End Date of reservation. Required. End date must be 10:00:00 am UTC")
    @ValidReservationEndDate
    @NotNull(message = "End Date is required")
    private Long endDate;
}
