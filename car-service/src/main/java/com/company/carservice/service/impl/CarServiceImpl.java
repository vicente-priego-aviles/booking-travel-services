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
import com.company.carservice.entity.Reservation;
import com.company.carservice.entity.Availability;
import com.company.carservice.entity.Car;
import com.company.carservice.repository.AvailabilityRepository;
import com.company.carservice.repository.ReservationRepository;
import com.company.carservice.helpers.BookingAvailabilityHelper;
import com.company.carservice.helpers.dto.BookingAvailabilityDto;
import com.company.carservice.repository.CarRepository;
import com.company.carservice.service.APIClient;
import com.company.carservice.service.CarService;
import com.company.carservice.stream.ReservationProducer;
import feign.FeignException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private ReservationProducer reservationProducer;
    private BookingAvailabilityHelper bookingAvailabilityHelper;

    @Override
    public List<CarDto> insertAll(List<CarDto> cars) {
        List<Car> carsEntity = cars.stream().map((car) -> modelMapper.map(car, Car.class)).toList();
        List<Car> savedList = new ArrayList<>();
        carsEntity.forEach((car) -> {
            List<Availability> availabilities = car.getAvailabilities();
            car.setAvailabilities(null);
            car = carRepository.save(car);
            Car finalCar = car;
            availabilities = availabilities.stream().peek((availability) -> availability.setCar(finalCar)).toList();
            availabilityRepository.saveAll(availabilities);
            savedList.add(car);
        });
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


    @Transactional
    String createAvailabilityAux(String id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Car car = carRepository.findById(reservation.getCar().getId()).orElse(null);
            if (car != null) {
                Availability availability = new Availability();
                availability.setStartDate(reservation.getStartDate());
                availability.setEndDate(reservation.getEndDate());
                availability.setCar(car);
                car.getAvailabilities().add(availability);
                carRepository.save(car);

                availability = availabilityRepository.save(availability);
                LOGGER.info("CarServiceImpl: cancelReservation: car.getId() = " + car.getId());
                LOGGER.info("CarServiceImpl: cancelReservation: availability.getId() = " + availability.getId());


                reservation.setStatus(Status.CANCELLED);
                reservationRepository.save(reservation);

                return availability.getId();
            } else {
                LOGGER.error("CarServiceImpl: Invalid CAR ID in the reservation object");
            }
        } else {
            LOGGER.error("CarServiceImpl: Invalid RESERVATION ID was found");
        }
        return null;
    }

    @Transactional
    Availability createAvailability(String id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Car car = carRepository.findById(reservation.getCar().getId()).orElse(null);
            if (car != null) {
                Availability availability = new Availability();
                availability.setStartDate(reservation.getStartDate());
                availability.setEndDate(reservation.getEndDate());
                availability.setCar(car);

                availability = availabilityRepository.save(availability);

                return availability;
            } else {
                LOGGER.error("CarServiceImpl: Invalid CAR ID in the reservation object");
            }
        } else {
            LOGGER.error("CarServiceImpl: Invalid RESERVATION ID was found");
        }
        return null;
    }

    @Override
    public void cancelReservation(String id) {
        Availability availability = createAvailability(id);
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            Car car = carRepository.findById(reservation.getCar().getId()).orElse(null);
            if (car != null) {
                car.getAvailabilities().add(availability);
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

    public void cancelReservationAux(String id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null) {
            LOGGER.info("CarServiceImpl - cancelReservation: reservation.getId() = " + reservation.getId());
            LOGGER.info("CarServiceImpl - cancelReservation: reservation.getStartDate() = " + reservation.getStartDate());
            LOGGER.info("CarServiceImpl - cancelReservation: reservation.getEndDate() = " + reservation.getEndDate());
            Car car = carRepository.findById(reservation.getCar().getId()).orElse(null);
            if (car != null) {
                /*
                                                                            List<Car> carsEntity = cars.stream().map((car) -> modelMapper.map(car, Car.class)).toList();
                                                                                                List<Car> savedList = new ArrayList<>();
                carsEntity.forEach((car) -> {
                    List<Availability> availabilities = car.getAvailabilities();
                    car.setAvailabilities(null);
                    car = carRepository.save(car);
                    Car finalCar = car;
                    availabilities = availabilities.stream().peek((availability) -> availability.setCar(finalCar)).toList();
                    availabilityRepository.saveAll(availabilities);
                    savedList.add(car);
                });

                 */
                LOGGER.info("CarServiceImpl - cancelReservation: reservation.getCar().getId() = " + reservation.getCar().getId());

                Availability newAvailability = new Availability();
                newAvailability.setStartDate(reservation.getStartDate());
                newAvailability.setEndDate(reservation.getEndDate());
                List<Availability> availabilities = car.getAvailabilities();
                availabilities.add(newAvailability);
                car.setAvailabilities(null);
                car = carRepository.save(car);
                Car finalCar = car;
                availabilities = availabilities.stream().peek((availability -> availability.setCar(finalCar))).toList();
                availabilityRepository.saveAll(availabilities);



                //availability.setCar(car);
                /*LOGGER.info("CarServiceImpl - cancelReservation: availability.getStartDate() = " + availability.getStartDate());
                LOGGER.info("CarServiceImpl - cancelReservation: availability.getEndDate() = " + availability.getEndDate());
                //LOGGER.info("CarServiceImpl - cancelReservation: availability.getCar().getId() = " + availability.getCar().getId());
                //car = carRepository.findById(reservation.getCar().getId()).orElse(null);
                //availabilityRepository.save(availability);
                if (car != null) {
                    /*List<Availability> availabilities = new ArrayList<>();
                    List<Availability> availabilitiesAux = car.getAvailabilities();
                    for(Availability aux : availabilitiesAux) {
                        Availability a = new Availability();
                        a.setStartDate(aux.getStartDate());
                        a.setEndDate(aux.getEndDate());
                        a.setCar(car);
                    }*/
                    //availabilities.add(availability);
                    /*car.getAvailabilities().add(availability);
                    //car.setAvailabilities(availabilities);
                    LOGGER.info("CarServiceImpl - cancelReservation: car.getId() = " + car.getId());
                    LOGGER.info("CarServiceImpl - cancelReservation: car.getBrand() = " + car.getBrand());
                    LOGGER.info("CarServiceImpl - cancelReservation: car.getModel() = " + car.getModel());
                    LOGGER.info("CarServiceImpl - cancelReservation: car.getAvailabilities.size() = " + car.getAvailabilities().size());
                    carRepository.save(car);
                }*/
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
