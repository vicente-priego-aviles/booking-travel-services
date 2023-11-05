package com.company.paymentservice.service.impl;

import com.company.basedomains.dto.Status;
import com.company.basedomains.exception.NotBookableException;
import com.company.basedomains.dto.ReservationDto;
import com.company.basedomains.exception.PaymentException;
import com.company.paymentservice.repository.ReservationRepository;
import com.company.paymentservice.stream.ReservationProducer;
import com.company.paymentservice.entity.Reservation;
import com.company.paymentservice.helpers.ReservationType;
import com.company.paymentservice.service.PaymentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private ReservationRepository reservationRepository;
    private ModelMapper modelMapper;
    private ReservationProducer reservationProducer;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationDto.class)).toList();
    }

    @Override
    public ReservationDto payReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new NotBookableException("RESERVATION", "id", id.toString()));
        double probability = Math.random();
        LOGGER.debug("PaymentServiceImpl: payReservation: probability = {}", probability);
        if (reservation.getFlightBooked() == null || !reservation.getFlightBooked()) {
            throw new PaymentException("FLIGHT", "id", reservation.getId().toString());
        }
        if (reservation.getHotelBooked() == null || !reservation.getHotelBooked()) {
            throw new PaymentException("HOTEL", "id", reservation.getId().toString());
        }
        if (reservation.getCarBooked() == null || !reservation.getCarBooked()) {
            throw new PaymentException("CAR", "id", reservation.getId().toString());
        }
        if (reservation.getStatus() != Status.IN_PROGRESS) {
            return modelMapper.map(reservation, ReservationDto.class);
        }

        if (probability < 0.5) {
            // Reservations Paid
            reservation.setStatus(Status.PAID);
        } else {
            // Reservations Cancelled
            reservation.setStatus(Status.CANCELLED);
        }
        reservation = reservationRepository.save(reservation);
        reservationProducer.send(modelMapper.map(reservation, ReservationDto.class));
        return modelMapper.map(reservation, ReservationDto.class);
    }

    public void addReservation(UUID id, ReservationType reservationType) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            reservation = new Reservation();
            reservation.setId(id);
            reservation.setStatus(Status.IN_PROGRESS);
        }
        if (reservationType == ReservationType.FLIGHT) {
            reservation.setFlightBooked(true);
        } else if (reservationType == ReservationType.HOTEL) {
            reservation.setHotelBooked(true);
        } else if (reservationType == ReservationType.CAR) {
            reservation.setCarBooked(true);
        }
        reservationRepository.save(reservation);
    }
}
