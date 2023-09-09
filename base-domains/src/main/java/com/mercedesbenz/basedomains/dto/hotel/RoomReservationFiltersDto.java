package com.mercedesbenz.basedomains.dto.hotel;

import com.mercedesbenz.basedomains.constraints.ValidReservationEndDate;
import com.mercedesbenz.basedomains.constraints.ValidReservationStartDate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RoomReservationFiltersDto {
    @NotNull(message = "reservation ID is required")
    private UUID reservationID;

    @ValidReservationStartDate
    @NotNull(message = "Start Date is required")
    private Long startDate;

    @ValidReservationEndDate
    @NotNull(message = "End Date is required")
    private Long endDate;
}
