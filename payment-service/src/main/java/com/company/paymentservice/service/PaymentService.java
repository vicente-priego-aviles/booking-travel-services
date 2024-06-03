package com.company.paymentservice.service;

import com.company.basedomains.dto.ReservationDto;
import com.company.paymentservice.helpers.ReservationType;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    public List<ReservationDto> getAllReservations();
    public ReservationDto payReservation(String id);

    public void addReservation(String id, ReservationType reservationType);
}
