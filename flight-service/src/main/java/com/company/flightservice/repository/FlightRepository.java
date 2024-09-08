package com.company.flightservice.repository;

import com.company.flightservice.entity.Flight;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends Neo4jRepository<Flight, String> {
    public List<Flight> findAllByRemainingSeatsGreaterThan(Long remainingSeats);
}
