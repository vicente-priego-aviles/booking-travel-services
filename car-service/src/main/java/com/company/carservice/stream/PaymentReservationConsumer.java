package com.company.carservice.stream;

import com.company.basedomains.dto.ReservationDto;
import com.company.basedomains.dto.Status;
import com.company.carservice.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PaymentReservationConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentReservationConsumer.class);

    @Autowired
    private CarService carService;

    @Bean
    Consumer<Message<ReservationDto>> input() {
        return message -> {
            LOGGER.info("Headers: {}", message.getHeaders());
            LOGGER.info("Headers.Id: {}", message.getHeaders().getId());
            LOGGER.info("ReservationDto: {}", message.getPayload());
            LOGGER.info("message.getPayload().getId(): " + message.getPayload().getId());
            LOGGER.info("message.getPayload().getStatus(): " + message.getPayload().getStatus());
            LOGGER.info("message.getPayload().getCarBooked(): " + message.getPayload().getCarBooked());
            LOGGER.info("message.getPayload().getFlightBooked(): " + message.getPayload().getFlightBooked());
            LOGGER.info("message.getPayload().getHotelBooked(): " + message.getPayload().getHotelBooked());
            if (message.getPayload().getStatus() != null) {
                if (message.getPayload().getStatus() == Status.CANCELLED) {
                    cancel(message.getPayload());
                } else {
                    update(message.getPayload());
                }
            }
        };
    }

    private void cancel(ReservationDto reservation) {
        carService.cancelReservation(reservation.getId());
    }

    private void update(ReservationDto reservation) {
        carService.updateReservationStatus(reservation.getId(), reservation.getStatus());
    }

}
