package com.company.carservice.neo4j.helpers.dto;

import com.company.carservice.neo4j.entity.Availability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("neo4j")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingAvailabilityDto {
    private Availability availabilityBookable;
    private Availability availabilityBeforeReservation;
    private Availability availabilityAfterReservation;
    private List<Availability> availabilitiesToSaveWithCar;
}
