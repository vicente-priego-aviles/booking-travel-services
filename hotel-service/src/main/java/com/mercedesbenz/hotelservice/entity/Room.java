package com.mercedesbenz.hotelservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Room {
    @Id
    private UUID id;
    private String title;
    private Long peopleCapacity;
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Availability> availabilities;
    @ManyToOne
    private Hotel hotel;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
