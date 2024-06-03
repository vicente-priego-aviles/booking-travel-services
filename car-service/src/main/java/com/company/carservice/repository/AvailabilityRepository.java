package com.company.carservice.repository;

import com.company.carservice.entity.Availability;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvailabilityRepository extends Neo4jRepository<Availability, String> {
}
