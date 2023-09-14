package com.mercedesbenz.paymentservice.service;

import com.mercedesbenz.basedomains.dto.ReservationDto;
import com.mercedesbenz.paymentservice.helpers.ReservationType;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    public List<ReservationDto> getAllReservations();
    public ReservationDto payReservation(UUID id);

    public void addReservation(UUID id, ReservationType reservationType);
}
