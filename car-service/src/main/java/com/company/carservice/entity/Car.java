package com.company.carservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = {"Car"})
public class Car {
    @Id
    @GeneratedValue
    private String id;

    private String brand;

    private String model;

    @Property(name = "cost_per_day")
    private Long costPerDay;

    @Property(name = "remaining_cars")
    private Long remainingCars;

}
