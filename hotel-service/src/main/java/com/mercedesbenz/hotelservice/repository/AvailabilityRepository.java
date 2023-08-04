package com.mercedesbenz.hotelservice.repository;

import com.mercedesbenz.hotelservice.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
}
