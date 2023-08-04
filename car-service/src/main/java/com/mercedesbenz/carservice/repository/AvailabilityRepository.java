package com.mercedesbenz.carservice.repository;

import com.mercedesbenz.carservice.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
}
