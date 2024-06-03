package com.company.carservice.repository;

import com.company.carservice.entity.Reservation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservationRepository extends Neo4jRepository<Reservation, String> {
}
