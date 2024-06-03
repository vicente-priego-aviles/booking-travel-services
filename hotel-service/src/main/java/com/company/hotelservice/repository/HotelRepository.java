package com.company.hotelservice.repository;

import com.company.hotelservice.entity.Hotel;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HotelRepository extends Neo4jRepository<Hotel, String> {
}
