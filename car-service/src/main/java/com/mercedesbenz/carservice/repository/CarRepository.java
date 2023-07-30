package com.mercedesbenz.carservice.repository;

import com.mercedesbenz.carservice.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, UUID> {
}
