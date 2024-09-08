package com.company.flightservice.repository;

import com.company.flightservice.entity.Reservation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends Neo4jRepository<Reservation, String> {
}
