package com.company.carservice.service;

import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.cars.CarDto;
import com.company.basedomains.dto.cars.CarReservationFiltersDto;
import com.company.basedomains.dto.cars.ReservationDto;

import java.util.List;
import java.util.UUID;

public interface CarService {
    public List<CarDto> insertAll(List<CarDto> cars);
    public List<CarDto> findAll();
    public CarDto findOne(UUID id);
    public List<ReservationDto> getAllBookings();
    public ReservationDto bookCar(UUID carId, CarReservationFiltersDto carReservationFiltersDto);
    public void cancelReservation(UUID id);
    public void updateReservationStatus(UUID id, Status status);
}
