package com.company.hotelservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Availability {
    @Id
    @GeneratedValue
    private String id;
    @Property(name = "start_date")
    private Long startDate;
    @Property(name = "end_date")
    private Long endDate;
    @Relationship(type = "IS_AVAILABLE", direction = Relationship.Direction.INCOMING)
    private Room room;
}
