package com.mercedesbenz.basedomains.dto.cars;

import com.mercedesbenz.basedomains.constraints.ValidReservationEndDate;
import com.mercedesbenz.basedomains.constraints.ValidReservationStartDate;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CarReservationFiltersDto {
    @NotEmpty(message = "Start date is required")
    @ValidReservationStartDate
    private Long startDate;
    @NotEmpty(message = "End date is required")
    @ValidReservationEndDate
    private Long endDate;
}
