package com.company.flightservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = {"Flight"})
public class Flight {
    @Id
    @GeneratedValue
    private String id;
    private String reference;
    private String airline;
    @Property(name = "departure_airport_code")
    private String departureAirportCode;
    @Property(name = "departure_airport_name")
    private String departureAirportName;
    @Property(name = "departure_time")
    private Long departureTime;
    @Property(name = "arrival_airport_code")
    private String arrivalAirportCode;
    @Property(name = "arrival_airport_name")
    private String arrivalAirportName;
    @Property(name = "arrival_time")
    private Long arrivalTime;
    @Property(name = "remaining_seats")
    private Long remainingSeats;
    private Long cost;
}
