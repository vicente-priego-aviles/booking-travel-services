package com.company.carservice.neo4j.repository;

import com.company.carservice.neo4j.entity.Reservation;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Profile("neo4j")
@Repository
public interface ReservationRepository extends Neo4jRepository<Reservation, UUID> {
}
