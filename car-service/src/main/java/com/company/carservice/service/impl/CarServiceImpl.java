package com.company.carservice.service.impl;

import com.company.basedomains.dto.ResponseDto;
import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.cars.CarDto;
import com.company.basedomains.dto.cars.CarReservationFiltersDto;
import com.company.basedomains.dto.cars.ReservationDto;
import com.company.basedomains.exception.BookingTravelException;
import com.company.basedomains.exception.NotBookableException;
import com.company.basedomains.exception.ResourceNotFoundException;
import com.company.basedomains.exception.ServiceException;
import com.company.carservice.entity.Car;
import com.company.carservice.entity.Reservation;
import com.company.carservice.repository.CarRepository;
import com.company.carservice.repository.ReservationRepository;
import com.company.carservice.service.APIClient;
import com.company.carservice.service.CarService;
import com.company.carservice.stream.ReservationProducer;
import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private final Logger LOGGER = LoggerFactory.getLogger(CarServiceImpl.class);

    private APIClient apiClient;
    private ModelMapper modelMapper;
    private CarRepository carRepository;
    private ReservationRepository reservationRepository;
    private ReservationProducer reservationProducer;

    @Override
    public List<CarDto> insertAll(List<CarDto> cars) {
        List<Car> carsEntity = cars.stream().map((car) -> modelMapper.map(car, Car.class)).toList();
        List<Car> savedList = carRepository.saveAll(carsEntity);
        return savedList.stream().map(car -> modelMapper.map(car, CarDto.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> findAll() {
        List<Car> cars = carRepository.findAll();
        return cars.stream().map(car -> modelMapper.map(car, CarDto.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CarDto findOne(String id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CAR", "id", id));
        return modelMapper.map(car, CarDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getAllBookings() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationDto.class)).toList();
    }

    @Retry(name = "${spring.application.name}", fallbackMethod = "bookCarCircuitBreakerFallback")
    @Override
    public ReservationDto bookCar(String carId, CarReservationFiltersDto carReservationFiltersDto) {
        LOGGER.info("CarServiceImpl.bookCar: trying to use FLIGHT-SERVICE API");
        ResponseDto reservationResponse = apiClient.bookCheckReservationID(carReservationFiltersDto.getReservationID());
        LOGGER.info("CarServiceImpl.bookCar: ended call to FLIGHT-SERVICE API");

        String reservationID = null;
        if (reservationResponse != null && reservationResponse.getData() != null) {
            reservationID = reservationResponse.getData().toString();
        }
        if (reservationID == null) {
            throw new NotBookableException("CAR", "reservationID", carReservationFiltersDto.getReservationID());
        }

        Car car = carRepository.findById(carId).orElseThrow(() -> new ResourceNotFoundException("CAR", "id", carId));

        if (car.getRemainingCars() < 1) {
            throw new NotBookableException("NO CAR REMAINING", "reservationID", carReservationFiltersDto.getReservationID());
        }
        car.setRemainingCars(car.getRemainingCars() - 1);
        car = carRepository.save(car);

        Reservation reservation = new Reservation();
        reservation.setId(carReservationFiltersDto.getReservationID());
        reservation.setCar(car);
        reservation.setStatus(Status.IN_PROGRESS);
        reservationRepository.save(reservation);

        reservationProducer.send(modelMapper.map(reservation, ReservationDto.class));

        return modelMapper.map(reservation, ReservationDto.class);
    }

    public ReservationDto bookCarCircuitBreakerFallback(String carId, CarReservationFiltersDto carReservationFiltersDto, Throwable exception) throws Throwable {
        LOGGER.error("Exception handled by CarServiceImpl.bookCarCircuitBreakerFallback", exception);
        if (exception instanceof BookingTravelException) {
            throw exception;
        } else if (exception instanceof FeignException) {
            throw new NotBookableException("RESERVATION", "id", carReservationFiltersDto.getReservationID());
        } else {
            throw new ServiceException("FLIGHT-SERVICE");
        }
    }

    @Override
    public void cancelReservation(String id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Car car = carRepository.findById(reservation.getCar().getId()).orElse(null);
            if (car != null) {
                car.setRemainingCars(car.getRemainingCars() + 1);
                carRepository.save(car);

                reservation.setStatus(Status.CANCELLED);
                reservationRepository.save(reservation);
            } else {
                LOGGER.error("CarServiceImpl: Invalid CAR ID in the reservation object");
            }
        } else {
            LOGGER.error("CarServiceImpl: Invalid RESERVATION ID was found");
        }
    }

    @Override
    public void updateReservationStatus(String id, Status status) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            reservation.setStatus(status);
            reservationRepository.save(reservation);
        }
    }
}
