package com.mercedesbenz.carservice.service;

import com.mercedesbenz.basedomains.dto.cars.CarDto;
import com.mercedesbenz.basedomains.dto.cars.CarReservationFiltersDto;
import com.mercedesbenz.basedomains.dto.cars.ReservationDto;

import java.util.List;
import java.util.UUID;

public interface CarService {
    public List<CarDto> insertAll(List<CarDto> cars);
    public List<CarDto> findAll();
    public CarDto findOne(UUID id);
    public List<ReservationDto> getAllBookings();
    public ReservationDto bookCar(UUID carId, CarReservationFiltersDto carReservationFiltersDto);
}
