package com.mercedesbenz.paymentservice.repository;

import com.mercedesbenz.paymentservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
}
