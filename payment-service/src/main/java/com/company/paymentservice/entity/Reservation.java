package com.company.paymentservice.entity;

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
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean flightBooked;
    private boolean hotelBooked;
    private boolean carBooked;
}
