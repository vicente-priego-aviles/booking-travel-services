package com.mercedesbenz.paymentservice.service;

import com.mercedesbenz.basedomains.dto.ReservationDto;
import com.mercedesbenz.paymentservice.helpers.ReservationType;

import java.util.UUID;

public interface PaymentService {

    public ReservationDto payReservation(UUID id);

    public void addFlightReservation(UUID id, ReservationType reservationType);
}
