package com.company.flightservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Flight {
    @Id
    private UUID id;
    private String reference;
    private String airline;
    private String departureAirportCode;
    private String departureAirportName;
    private Long departureTime;
    private String arrivalAirportCode;
    private String arrivalAirportName;
    private Long arrivalTime;
    private Long remainingSeats;
    private Long cost;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
