package com.company.carservice.neo4j.entity;

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
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = {"Availability"})
public class Availability {
    @Id
    @GeneratedValue(generatorClass = GeneratedValue.UUIDGenerator.class)
    private UUID id;

    @Property(name = "start_date")
    private Long startDate;

    @Property(name = "end_date")
    private Long endDate;

    @Relationship(type = "IS_AVAILABLE", direction = Relationship.Direction.OUTGOING)
    private Car car;
}
