package com.mercedesbenz.carservice.service;

import com.mercedesbenz.basedomains.dto.CarDto;

import java.util.List;

public interface CarService {

    public List<CarDto> insertAll(List<CarDto> cars);

}
