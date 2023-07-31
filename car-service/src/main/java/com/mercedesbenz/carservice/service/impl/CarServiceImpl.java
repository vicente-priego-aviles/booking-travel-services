package com.mercedesbenz.carservice.service.impl;

import com.mercedesbenz.basedomains.dto.CarDto;
import com.mercedesbenz.carservice.entity.Car;
import com.mercedesbenz.carservice.repository.CarRepository;
import com.mercedesbenz.carservice.service.CarService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarServiceImpl.class);

    private ModelMapper modelMapper;

    private CarRepository carRepository;

    @Override
    public List<CarDto> insertAll(List<CarDto> cars) {
        Iterable<Car> carsEntity = cars.stream().map((car) -> modelMapper.map(car, Car.class)).collect(Collectors.toSet());
        List<Car> savedList = carRepository.saveAll(carsEntity);
        return savedList.stream().map((car) -> modelMapper.map(car, CarDto.class)).toList();
    }

    @Override
    public List<CarDto> findAll() {
        List<Car> cars = carRepository.findAll();
        return cars.stream().map((car) -> modelMapper.map(car, CarDto.class)).toList();
    }

    @Override
    public CarDto findOne(UUID id) {
        Car car = carRepository.findById(id).orElse(null);
        return (car != null ? modelMapper.map(car, CarDto.class) : null);
    }
}
