package com.mercedesbenz.basedomains.dto.hotel;

import com.mercedesbenz.basedomains.constraints.ValidReservationEndDate;
import com.mercedesbenz.basedomains.constraints.ValidReservationStartDate;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RoomReservationFiltersDto {
    @NotEmpty(message = "Start Date is required")
    @ValidReservationStartDate
    private Long startDate;
    @NotEmpty(message = "End Date is required")
    @ValidReservationEndDate
    private Long endDate;
}
