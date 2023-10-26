package com.mercedesbenz.flightservice.stream;

import com.mercedesbenz.basedomains.dto.flight.ReservationDto;
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
        LOGGER.info("Reservation sent from FLIGHTS service: " + reservationDto.getId());
        streamBridge.send("output", reservationDto);
    }
}
