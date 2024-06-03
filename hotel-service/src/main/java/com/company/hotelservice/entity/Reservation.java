package com.company.hotelservice.entity;

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
@Node(labels = {"HotelReservation"})
public class Reservation {
    @Id
    private String id;

    @Relationship(type = "BOOKED_ROOM", direction = Relationship.Direction.INCOMING)
    private Room room;

    @Property(name = "start_date")
    private Long startDate;

    @Property(name = "end_date")
    private Long endDate;

    private Status status;
}
