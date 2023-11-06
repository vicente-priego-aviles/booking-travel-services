package com.company.paymentservice.stream;

import com.company.basedomains.dto.ReservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class ReservationProducer {
    private final Logger LOGGER = LoggerFactory.getLogger(ReservationProducer.class);

    @Autowired
    private StreamBridge streamBridge;

    public void send(ReservationDto reservationDto) {
        LOGGER.info("Reservation sent from PAYMENT service: " + reservationDto.getId());
        streamBridge.send("output", reservationDto);
    }
}
