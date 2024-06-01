package com.company.carservice.neo4j.entity;

import com.company.basedomains.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.schema.*;

import java.util.UUID;

@Profile("neo4j")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node(labels = {"Reservation"})
public class Reservation {

    @Id
    @GeneratedValue(generatorClass = GeneratedValue.UUIDGenerator.class)
    private UUID id;

    @Relationship(type = "BOOKED_CAR", direction = Relationship.Direction.INCOMING)
    private Car car;

    @Property(name = "start_date")
    private Long startDate;

    @Property(name = "end_date")
    private Long endDate;

    private Status status;
}
