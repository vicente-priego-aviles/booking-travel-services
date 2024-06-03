package com.company.paymentservice.entity;

import com.company.basedomains.dto.Status;
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
@AllArgsConstructor
@NoArgsConstructor
@Node(labels = {"Reservation"})
public class Reservation {
    @Id
    private String id;
    private Status status;
    @Property(name = "flight_booked")
    private boolean flightBooked;
    @Property(name = "hotel_booked")
    private boolean hotelBooked;
    @Property(name = "car_booked")
    private boolean carBooked;
}
