package com.mercedesbenz.carservice.service.impl;

import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.basedomains.dto.cars.CarDto;
import com.mercedesbenz.basedomains.dto.cars.CarReservationFiltersDto;
import com.mercedesbenz.basedomains.dto.cars.ReservationDto;
import com.mercedesbenz.basedomains.exceptions.BookingTravelException;
import com.mercedesbenz.basedomains.exceptions.NotBookableException;
import com.mercedesbenz.basedomains.exceptions.ResourceNotFoundException;
import com.mercedesbenz.carservice.entity.Availability;
import com.mercedesbenz.carservice.entity.Car;
import com.mercedesbenz.carservice.entity.Reservation;
import com.mercedesbenz.carservice.repository.AvailabilityRepository;
import com.mercedesbenz.carservice.repository.CarRepository;
import com.mercedesbenz.carservice.repository.ReservationRepository;
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
    private ModelMapper modelMapper;
    private CarRepository carRepository;
    private AvailabilityRepository availabilityRepository;
    private ReservationRepository reservationRepository;

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
        Car car = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CAR", "id", id.toString()));
        return modelMapper.map(car, CarDto.class);
    }

    @Override
    public List<ReservationDto> getAllBookings() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationDto.class)).toList();
    }

    @Override
    public ReservationDto bookCar(UUID carId, CarReservationFiltersDto carReservationFiltersDto) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new ResourceNotFoundException("CAR", "id", carId.toString()));
        Availability availabilityBookable = null;
        Reservation reservation = null;
        if (car != null && car.getAvailabilities() != null) {
            for (Availability availability : car.getAvailabilities()) {
                if (availability.getStartDate() <= carReservationFiltersDto.getStartDate() && availability.getEndDate() > carReservationFiltersDto.getEndDate()) {
                    availabilityBookable = availability;
                }
            }
            if (availabilityBookable != null) {
                if ((availabilityBookable.getStartDate() + 1000*60*60*24) <= carReservationFiltersDto.getStartDate()) {
                    Availability availabilityBeforeStartReservation = new Availability();
                    availabilityBeforeStartReservation.setStartDate(availabilityBookable.getStartDate());
                    availabilityBeforeStartReservation.setEndDate(carReservationFiltersDto.getStartDate() - 1000*60*60*24);
                    availabilityBeforeStartReservation.setCar(car);
                    availabilityRepository.save(availabilityBeforeStartReservation);
                }
                if ((availabilityBookable.getEndDate() - 1000*60*60*24) >= carReservationFiltersDto.getEndDate()) {
                    Availability availabilityAfterEndReservation = new Availability();
                    availabilityAfterEndReservation.setStartDate(carReservationFiltersDto.getEndDate() + 1000*60*60*24);
                    availabilityAfterEndReservation.setEndDate(availabilityBookable.getEndDate());
                    availabilityAfterEndReservation.setCar(car);
                    availabilityRepository.save(availabilityAfterEndReservation);
                }
                availabilityRepository.delete(availabilityBookable);
                reservation = new Reservation();
                reservation.setCar(car);
                reservation.setStartDate(carReservationFiltersDto.getStartDate());
                reservation.setEndDate(carReservationFiltersDto.getEndDate());
                reservation.setStatus(Status.IN_PROGRESS);
                reservationRepository.save(reservation);
            }
        } else {
            throw new NotBookableException("CAR", "id", carId.toString());
        }
        return modelMapper.map(car, ReservationDto.class);
    }
}
