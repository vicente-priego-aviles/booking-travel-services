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
    public CarDto findOne(String id);
    public List<ReservationDto> getAllBookings();
    public ReservationDto bookCar(String carId, CarReservationFiltersDto carReservationFiltersDto);
    public void cancelReservation(String id);
    public void updateReservationStatus(String id, Status status);
}
