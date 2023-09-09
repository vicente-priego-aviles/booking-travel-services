package com.mercedesbenz.paymentservice.entity;

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
    @Enumerated(EnumType.STRING)
    private Status status;
    private Boolean flightBooked;
    private Boolean hotelBooked;
    private Boolean carBooked;
}
