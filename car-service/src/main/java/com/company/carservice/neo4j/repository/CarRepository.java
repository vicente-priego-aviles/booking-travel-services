package com.company.carservice.neo4j.repository;

import com.company.carservice.neo4j.entity.Car;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Profile("neo4j")
@Qualifier("CarRepository")
@Repository
public interface CarRepository extends Neo4jRepository<Car, UUID> {
}
