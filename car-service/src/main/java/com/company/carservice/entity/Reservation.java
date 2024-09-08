package com.company.carservice.entity;

import com.company.basedomains.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node(labels = {"CarReservation"})
public class Reservation {

    @Id
    private String id;

    @Relationship(type = "BOOKED_CAR", direction = Relationship.Direction.INCOMING)
    private Car car;

    private Status status;
}
