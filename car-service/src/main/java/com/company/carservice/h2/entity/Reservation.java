package com.company.carservice.h2.entity;

import com.company.basedomains.dto.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Profile("h2")
public class Reservation {

    @Id
    private UUID id;

    @ManyToOne
    private Car car;

    private Long startDate;
    private Long endDate;
    @Enumerated(EnumType.STRING)
    private Status status;
}
