package com.mercedesbenz.carservice.service.impl;

import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.basedomains.dto.cars.CarDto;
import com.mercedesbenz.basedomains.dto.cars.CarReservationFiltersDto;
import com.mercedesbenz.basedomains.dto.cars.ReservationDto;
import com.mercedesbenz.basedomains.exception.NotBookableException;
import com.mercedesbenz.basedomains.exception.ResourceNotFoundException;
import com.mercedesbenz.basedomains.exception.ServiceException;
import com.mercedesbenz.carservice.entity.Availability;
import com.mercedesbenz.carservice.entity.Car;
import com.mercedesbenz.carservice.entity.Reservation;
import com.mercedesbenz.carservice.kafka.ReservationProducer;
import com.mercedesbenz.carservice.repository.AvailabilityRepository;
import com.mercedesbenz.carservice.repository.CarRepository;
import com.mercedesbenz.carservice.repository.ReservationRepository;
import com.mercedesbenz.carservice.service.APIClient;
import com.mercedesbenz.carservice.service.CarService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private final Logger LOGGER = LoggerFactory.getLogger(CarServiceImpl.class);

    private APIClient apiClient;
    private ModelMapper modelMapper;
    private CarRepository carRepository;
    private AvailabilityRepository availabilityRepository;
    private ReservationRepository reservationRepository;
    private ReservationProducer reservationProducer;

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

    @Retry(name = "${spring.application.name}", fallbackMethod = "bookCarCircuitBreakerFallback")
    @Override
    public ReservationDto bookCar(UUID carId, CarReservationFiltersDto carReservationFiltersDto) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new ResourceNotFoundException("CAR", "id", carId.toString()));
        Availability availabilityBookable = null;
        Reservation reservation = null;

        Calendar dateNormalizator = Calendar.getInstance();

        dateNormalizator.setTimeInMillis(carReservationFiltersDto.getStartDate());
        dateNormalizator.set(dateNormalizator.get(Calendar.YEAR), dateNormalizator.get(Calendar.MONTH), dateNormalizator.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
        carReservationFiltersDto.setStartDate(dateNormalizator.getTimeInMillis());

        dateNormalizator.setTimeInMillis(carReservationFiltersDto.getEndDate());
        dateNormalizator.set(dateNormalizator.get(Calendar.YEAR), dateNormalizator.get(Calendar.MONTH), dateNormalizator.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
        carReservationFiltersDto.setEndDate(dateNormalizator.getTimeInMillis());

        if (car != null && car.getAvailabilities() != null && !car.getAvailabilities().isEmpty()) {
            List<Availability> availabilitiesToSaveWithCar = new ArrayList<>();
            for (Availability availability : car.getAvailabilities()) {
                if (availability.getStartDate() <= carReservationFiltersDto.getStartDate() && availability.getEndDate() >= carReservationFiltersDto.getEndDate()) {
                    availabilityBookable = availability;
                } else {
                    availabilitiesToSaveWithCar.add(availability);
                }
            }
            if (availabilityBookable != null &&
            availabilityBookable.getStartDate() <= carReservationFiltersDto.getStartDate() &&
            carReservationFiltersDto.getEndDate() <= availabilityBookable.getEndDate()) {
                Availability availabilityBeforeReservation = null;
                Availability availabilityAfterReservation = null;
                Calendar updatedDate = Calendar.getInstance();

                Calendar availabilityBookableStartDate = Calendar.getInstance();
                Calendar availabilityBookableEndDate = Calendar.getInstance();
                Calendar carReservationFiltersDtoStartDate = Calendar.getInstance();
                Calendar carReservationFiltersDtoEndDate = Calendar.getInstance();
                availabilityBookableStartDate.setTimeInMillis(availabilityBookable.getStartDate());
                availabilityBookableEndDate.setTimeInMillis(availabilityBookable.getEndDate());
                carReservationFiltersDtoStartDate.setTimeInMillis(carReservationFiltersDto.getStartDate());
                carReservationFiltersDtoEndDate.setTimeInMillis(carReservationFiltersDto.getEndDate());

                if (availabilityBookableStartDate.get(Calendar.YEAR) == carReservationFiltersDtoStartDate.get(Calendar.YEAR) &&
                        availabilityBookableStartDate.get(Calendar.MONTH) == carReservationFiltersDtoStartDate.get(Calendar.MONTH) &&
                        availabilityBookableStartDate.get(Calendar.DAY_OF_MONTH) == carReservationFiltersDtoStartDate.get(Calendar.DAY_OF_MONTH) &&
                        availabilityBookableStartDate.get(Calendar.HOUR) == carReservationFiltersDtoStartDate.get(Calendar.HOUR) &&
                        availabilityBookableStartDate.get(Calendar.MINUTE) == carReservationFiltersDtoStartDate.get(Calendar.MINUTE)) {
                    availabilityAfterReservation = new Availability();
                    updatedDate.setTimeInMillis(carReservationFiltersDto.getEndDate());
                    updatedDate.set(updatedDate.get(Calendar.YEAR), updatedDate.get(Calendar.MONTH), updatedDate.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
                    availabilityAfterReservation.setStartDate(updatedDate.getTimeInMillis());
                    if (carReservationFiltersDtoEndDate.getTimeInMillis() <= availabilityBookableEndDate.getTimeInMillis()) {
                        updatedDate.setTimeInMillis(availabilityBookableEndDate.getTimeInMillis());
                        updatedDate.set(updatedDate.get(Calendar.YEAR), updatedDate.get(Calendar.MONTH), updatedDate.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
                        availabilityAfterReservation.setEndDate(updatedDate.getTimeInMillis());
                    } else {
                        availabilityAfterReservation = null;
                    }
                } else {
                    availabilityBeforeReservation = new Availability();
                    availabilityBeforeReservation.setStartDate(availabilityBookable.getStartDate());
                    updatedDate.setTimeInMillis(carReservationFiltersDto.getStartDate());
                    updatedDate.set(updatedDate.get(Calendar.YEAR), updatedDate.get(Calendar.MONTH), updatedDate.get(Calendar.DAY_OF_MONTH), 10, 0, 0);
                    availabilityBeforeReservation.setEndDate(updatedDate.getTimeInMillis());

                    if(carReservationFiltersDtoEndDate.getTimeInMillis() < availabilityBookableEndDate.getTimeInMillis()) {
                        availabilityAfterReservation = new Availability();
                        updatedDate.setTimeInMillis(carReservationFiltersDto.getEndDate());
                        updatedDate.set(updatedDate.get(Calendar.YEAR), updatedDate.get(Calendar.MONTH), updatedDate.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
                        availabilityAfterReservation.setStartDate(updatedDate.getTimeInMillis());
                        availabilityAfterReservation.setEndDate(availabilityBookable.getEndDate());
                    }
                }

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

                LOGGER.debug("CarServiceImpl.bookCar: trying to use FLIGHT-SERVICE API");
                ResponseDto reservationResponse = apiClient.bookCheckReservationID(carReservationFiltersDto.getReservationID());
                LOGGER.debug("CarServiceImpl.bookCar: ended call to FLIGHT-SERVICE API");

                UUID reservationID = null;
                if (reservationResponse != null && reservationResponse.getData() != null) {
                    reservationID = UUID.fromString(reservationResponse.getData().toString());
                }
                if (reservationID == null) {
                    throw new NotBookableException("ROOM", "reservationID", carReservationFiltersDto.getReservationID().toString());
                }
                reservation = new Reservation();
                reservation.setId(carReservationFiltersDto.getReservationID());
                reservation.setCar(car);
                reservation.setStartDate(carReservationFiltersDto.getStartDate());
                reservation.setEndDate(carReservationFiltersDto.getEndDate());
                reservation.setStatus(Status.IN_PROGRESS);
                reservationRepository.save(reservation);

                reservationProducer.send(modelMapper.map(reservation, ReservationDto.class));
            } else {
                throw new NotBookableException("CAR", "id", carId.toString());
            }
        } else {
            throw new NotBookableException("CAR", "id", carId.toString());
        }
        return modelMapper.map(reservation, ReservationDto.class);
    }

    public void bookCarCircuitBreakerFallback(Exception exception) {
        LOGGER.error("Exception handled by CarServiceImpl.bookCarCircuitBreakerFallback", exception);
        throw new ServiceException("FLIGHT-SERVICE");
    }

    @Override
    public void cancelReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Car car = reservation.getCar();
            Availability availability = new Availability();
            availability.setStartDate(reservation.getStartDate());
            availability.setEndDate(reservation.getEndDate());
            availability.setCar(car);
            availabilityRepository.save(availability);
            reservation.setStatus(Status.CANCELLED);
            reservationRepository.save(reservation);
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
