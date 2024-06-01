package com.company.carservice.h2.repository;

import com.company.carservice.h2.entity.Availability;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Profile("h2")
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
}
