package com.mercedesbenz.carservice.repository;

import com.mercedesbenz.carservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
}
