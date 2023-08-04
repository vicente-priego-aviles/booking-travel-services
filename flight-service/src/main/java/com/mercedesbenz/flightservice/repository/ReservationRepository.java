package com.mercedesbenz.flightservice.repository;

import com.mercedesbenz.flightservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
}
