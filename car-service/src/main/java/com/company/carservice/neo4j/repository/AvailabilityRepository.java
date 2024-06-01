package com.company.carservice.neo4j.repository;

import com.company.carservice.neo4j.entity.Availability;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Profile("neo4j")
@Repository
public interface AvailabilityRepository extends Neo4jRepository<Availability, UUID> {
}
