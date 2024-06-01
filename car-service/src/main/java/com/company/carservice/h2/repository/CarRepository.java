package com.company.carservice.h2.repository;

import com.company.carservice.h2.entity.Car;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Profile("h2")
@Qualifier("CarRepository")
public interface CarRepository extends JpaRepository<Car, UUID> {
}
