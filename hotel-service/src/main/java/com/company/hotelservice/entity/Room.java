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
public class Room {
    @Id
    @GeneratedValue
    private String id;
    private String title;
    @Property(name = "people_capacity")
    private Long peopleCapacity;

    @Relationship(type = "IS_AVAILABLE", direction = Relationship.Direction.OUTGOING)
    private List<Availability> availabilities;

    @Relationship(type = "ROOM_BELONGS_TO_HOTEL", direction = Relationship.Direction.OUTGOING)
    private Hotel hotel;
}
