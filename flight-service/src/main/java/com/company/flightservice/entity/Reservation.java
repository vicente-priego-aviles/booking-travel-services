package com.company.flightservice.entity;

import com.company.basedomains.dto.Status;
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
    private Flight flight;
    @Enumerated(EnumType.STRING)
    private Status status;
}
