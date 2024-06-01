package com.company.carservice.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;
import java.util.UUID;

@Profile("neo4j")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = {"Car"})
public class Car {
    @Id
    @GeneratedValue(generatorClass = GeneratedValue.UUIDGenerator.class)
    private UUID id;

    private String brand;

    private String model;

    private String license;

    @Property(name = "cost_per_day")
    private Long costPerDay;

    @Relationship(type = "IS_AVAILABLE", direction = Relationship.Direction.INCOMING)
    private List<Availability> availabilities;

}
