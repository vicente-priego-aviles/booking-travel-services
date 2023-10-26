package com.mercedesbenz.paymentservice.stream;

import com.mercedesbenz.basedomains.dto.cars.ReservationDto;
import com.mercedesbenz.paymentservice.helpers.ReservationType;
import com.mercedesbenz.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CarReservationConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(CarReservationConsumer.class);

    @Autowired
    private PaymentService paymentService;

    @Bean
    Consumer<Message<ReservationDto>> inputCars() {
        return message -> {
            LOGGER.info("Headers: {}", message.getHeaders());
            LOGGER.info("Headers.Id: {}", message.getHeaders().getId());
            LOGGER.info("ReservationDto: {}", message.getPayload());
            LOGGER.info("message.getPayload().getId(): " + message.getPayload().getId());
            LOGGER.info("message.getPayload().getStatus(): " + message.getPayload().getStatus());
            LOGGER.info("message.getPayload().getStartDate(): " + message.getPayload().getStartDate());
            LOGGER.info("message.getPayload().getEndDate(): " + message.getPayload().getEndDate());
            LOGGER.info("message.getPayload().getCar().getId(): " + message.getPayload().getCar().getId());
            LOGGER.info("message.getPayload().getCar().getBrand(): " + message.getPayload().getCar().getBrand());
            LOGGER.info("message.getPayload().getCar().getModel(): " + message.getPayload().getCar().getModel());
            paymentService.addReservation(message.getPayload().getId(), ReservationType.CAR);
        };
    }

}
