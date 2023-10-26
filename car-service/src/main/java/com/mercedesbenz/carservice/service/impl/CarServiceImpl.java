package com.mercedesbenz.carservice.service.impl;

import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.basedomains.dto.cars.CarDto;
import com.mercedesbenz.basedomains.dto.cars.CarReservationFiltersDto;
import com.mercedesbenz.basedomains.dto.cars.ReservationDto;
import com.mercedesbenz.basedomains.exception.BookingTravelException;
import com.mercedesbenz.basedomains.exception.NotBookableException;
import com.mercedesbenz.basedomains.exception.ResourceNotFoundException;
import com.mercedesbenz.basedomains.exception.ServiceException;
import com.mercedesbenz.carservice.helpers.BookingAvailabilityHelper;
import com.mercedesbenz.carservice.helpers.dto.BookingAvailabilityDto;
import com.mercedesbenz.carservice.entity.Availability;
import com.mercedesbenz.carservice.entity.Car;
import com.mercedesbenz.carservice.entity.Reservation;
//import com.mercedesbenz.carservice.kafka.ReservationProducer;
import com.mercedesbenz.carservice.repository.AvailabilityRepository;
import com.mercedesbenz.carservice.repository.CarRepository;
import com.mercedesbenz.carservice.repository.ReservationRepository;
import com.mercedesbenz.carservice.service.APIClient;
import com.mercedesbenz.carservice.service.CarService;
import com.mercedesbenz.carservice.stream.ReservationProducer;
import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private final Logger LOGGER = LoggerFactory.getLogger(CarServiceImpl.class);

    private APIClient apiClient;
    private ModelMapper modelMapper;
    private CarRepository carRepository;
    private AvailabilityRepository availabilityRepository;
    private ReservationRepository reservationRepository;
    //private ReservationProducer reservationProducer;
    private ReservationProducer reservationProducer;
    private BookingAvailabilityHelper bookingAvailabilityHelper;

    @Override
    public List<CarDto> insertAll(List<CarDto> cars) {
        Iterable<Car> carsEntity = cars.stream().map((car) -> modelMapper.map(car, Car.class)).collect(Collectors.toSet());
        List<Car> savedList = carRepository.saveAll(carsEntity);
        return savedList.stream().map((car) -> modelMapper.map(car, CarDto.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> findAll() {
        List<Car> cars = carRepository.findAll();
        return cars.stream().map((car) -> modelMapper.map(car, CarDto.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CarDto findOne(UUID id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CAR", "id", id.toString()));
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
    public ReservationDto bookCar(UUID carId, CarReservationFiltersDto carReservationFiltersDto) {
        LOGGER.info("CarServiceImpl.bookCar: trying to use FLIGHT-SERVICE API");
        ResponseDto reservationResponse = apiClient.bookCheckReservationID(carReservationFiltersDto.getReservationID());
        LOGGER.info("CarServiceImpl.bookCar: ended call to FLIGHT-SERVICE API");

        UUID reservationID = null;
        if (reservationResponse != null && reservationResponse.getData() != null) {
            reservationID = UUID.fromString(reservationResponse.getData().toString());
        }
        if (reservationID == null) {
            throw new NotBookableException("CAR", "reservationID", carReservationFiltersDto.getReservationID().toString());
        }

        Car car = carRepository.findById(carId).orElseThrow(() -> new ResourceNotFoundException("CAR", "id", carId.toString()));
        BookingAvailabilityDto bookingAvailabilityDto = bookingAvailabilityHelper.calculateAvailabilities(car, carReservationFiltersDto);
        Availability availabilityBeforeReservation = bookingAvailabilityDto.getAvailabilityBeforeReservation();
        Availability availabilityAfterReservation = bookingAvailabilityDto.getAvailabilityAfterReservation();
        Availability availabilityBookable = bookingAvailabilityDto.getAvailabilityBookable();
        List<Availability> availabilitiesToSaveWithCar = bookingAvailabilityDto.getAvailabilitiesToSaveWithCar();

        if (availabilityBeforeReservation != null) {
            availabilityBeforeReservation.setCar(car);
            availabilitiesToSaveWithCar.add(availabilityBeforeReservation);
            availabilityRepository.save(availabilityBeforeReservation);
        }
        if (availabilityAfterReservation != null) {
            availabilityAfterReservation.setCar(car);
            availabilitiesToSaveWithCar.add(availabilityAfterReservation);
            availabilityRepository.save(availabilityAfterReservation);
        }
        availabilityRepository.delete(availabilityBookable);
        car.setAvailabilities(availabilitiesToSaveWithCar);
        car = carRepository.save(car);

        Reservation reservation = new Reservation();
        reservation.setId(carReservationFiltersDto.getReservationID());
        reservation.setCar(car);
        reservation.setStartDate(carReservationFiltersDto.getStartDate());
        reservation.setEndDate(carReservationFiltersDto.getEndDate());
        reservation.setStatus(Status.IN_PROGRESS);
        reservationRepository.save(reservation);

        reservationProducer.send(modelMapper.map(reservation, ReservationDto.class));

        return modelMapper.map(reservation, ReservationDto.class);
    }

    public ReservationDto bookCarCircuitBreakerFallback(UUID carId, CarReservationFiltersDto carReservationFiltersDto, Throwable exception) throws Throwable {
        LOGGER.error("Exception handled by CarServiceImpl.bookCarCircuitBreakerFallback", exception);
        if (exception instanceof BookingTravelException) {
            throw exception;
        } else if (exception instanceof FeignException) {
            throw new NotBookableException("RESERVATION", "id", carReservationFiltersDto.getReservationID().toString());
        } else {
            throw new ServiceException("FLIGHT-SERVICE");
        }
    }

    @Override
    public void cancelReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            LOGGER.info("CarServiceImpl - cancelReservation: reservation.getId() = " + reservation.getId());
            LOGGER.info("CarServiceImpl - cancelReservation: reservation.getStartDate() = " + reservation.getStartDate());
            LOGGER.info("CarServiceImpl - cancelReservation: reservation.getEndDate() = " + reservation.getEndDate());
            Car car = carRepository.findById(reservation.getCar().getId()).orElse(null);
            if (car != null) {
                LOGGER.info("CarServiceImpl - cancelReservation: reservation.getCar().getId() = " + reservation.getCar().getId());
                Availability availability = new Availability();
                availability.setStartDate(reservation.getStartDate());
                availability.setEndDate(reservation.getEndDate());
                availability.setCar(car);
                availability = availabilityRepository.save(availability);
                List<Availability> availabilities = car.getAvailabilities();
                availabilities.add(availability);
                car.setAvailabilities(availabilities);
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
    public void updateReservationStatus(UUID id, Status status) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            reservation.setStatus(status);
            reservationRepository.save(reservation);
        }
    }
}
