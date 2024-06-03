package com.company.carservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = {"Availability"})
public class Availability {
    @Id
    @GeneratedValue
    private String id;

    @Property(name = "start_date")
    private Long startDate;

    @Property(name = "end_date")
    private Long endDate;

    @Relationship(type = "HAS_AVAILABILITY", direction = Relationship.Direction.OUTGOING)
    private Car car;
}
