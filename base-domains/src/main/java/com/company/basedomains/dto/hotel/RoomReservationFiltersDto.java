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
}
