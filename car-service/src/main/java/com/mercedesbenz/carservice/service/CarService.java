package com.mercedesbenz.carservice.service;

import com.mercedesbenz.basedomains.dto.CarDto;

import java.util.List;
import java.util.UUID;

public interface CarService {
    public List<CarDto> insertAll(List<CarDto> cars);

    public List<CarDto> findAll();
    public CarDto findOne(UUID id);
}
