package com.company.hotelservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue
    private String id;
    private String name;
    private String direction;
    @Property(name = "cost_per_night")
    private Long costPerNight;

    @Relationship(type = "HAS_ROOMS", direction = Relationship.Direction.INCOMING)
    private List<Room> rooms;
}
