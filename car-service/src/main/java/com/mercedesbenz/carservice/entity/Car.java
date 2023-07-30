package com.mercedesbenz.carservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Car {
    @Id
    private UUID id;

    private String brand;

    private String model;

    private String license;

    private Long costPerDay;

    @OneToMany
    private List<Availability> availabilities;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
