package com.company.carservice.repository;

import com.company.carservice.entity.Car;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarRepository extends Neo4jRepository<Car, String> {
}
