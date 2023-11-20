package com.company.hotelservice.entity;

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
public class Hotel {
    @Id
    private UUID id;
    private String name;
    private String direction;
    private Long costPerNight;
    @OneToMany (cascade = CascadeType.PERSIST, mappedBy = "hotel")
    private List<Room> rooms;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
