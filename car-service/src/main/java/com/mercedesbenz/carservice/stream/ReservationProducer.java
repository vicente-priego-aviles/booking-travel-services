package com.mercedesbenz.carservice.stream;

import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.basedomains.dto.cars.ReservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReservationProducer {
    private final Logger LOGGER = LoggerFactory.getLogger(ReservationProducer.class);

    @Autowired
    private StreamBridge streamBridge;

    public void send(ReservationDto reservationDto) {
        /* THE CODE INSIDE THIS COMMENT IS THE GOOD ONE

        LOGGER.info("Reservation sent from CARS service: " + reservationDto.getId());
        streamBridge.send("output", reservationDto);*/
        LOGGER.info("ReservationDto mocked sent to PAYMENT consumer to check if producer and consuemr are working correctly");
        com.mercedesbenz.basedomains.dto.ReservationDto res = new com.mercedesbenz.basedomains.dto.ReservationDto();
        res.setStatus(Status.IN_PROGRESS);
        res.setId(UUID.randomUUID());
        res.setCarBooked(true);
        res.setFlightBooked(false);
        res.setHotelBooked(false);
        streamBridge.send("output", res);
    }
}
