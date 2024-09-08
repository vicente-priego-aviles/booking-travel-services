package com.company.basedomains.dto.cars;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarReservationFiltersDto {
    @Schema(description = "Reservation ID. Required. ID gotten after flight reservation.")
    @NotNull(message = "Reservation ID is required")
    private String reservationID;
}
