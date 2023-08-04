package com.mercedesbenz.hotelservice.entity;

import com.mercedesbenz.basedomains.dto.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Reservation {
    @Id
    private UUID id;
    @ManyToOne
    private Room room;
    private Long startDate;
    private Long endDate;
    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
