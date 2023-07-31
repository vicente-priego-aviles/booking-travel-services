package com.mercedesbenz.flightservice.repository;

import com.mercedesbenz.flightservice.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FlightRepository extends JpaRepository<Flight, UUID> {
}
