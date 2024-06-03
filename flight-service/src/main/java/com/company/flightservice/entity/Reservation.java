package com.company.flightservice.entity;

import com.company.basedomains.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node(labels = {"FlightReservation"})
public class Reservation {
    @Id
    private String id;

    @Relationship(type = "BOOKED_FLIGHT", direction = Relationship.Direction.INCOMING)
    private Flight flight;

    private Status status;
}
