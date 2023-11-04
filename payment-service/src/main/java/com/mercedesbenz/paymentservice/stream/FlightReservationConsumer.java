package com.mercedesbenz.paymentservice.stream;

import com.mercedesbenz.basedomains.dto.flight.ReservationDto;
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
public class FlightReservationConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(FlightReservationConsumer.class);

    @Autowired
    private PaymentService paymentService;

    @Bean
    Consumer<Message<ReservationDto>> inputFlight() {
        return message -> {
            LOGGER.info("Headers: {}", message.getHeaders());
            LOGGER.info("Headers.Id: {}", message.getHeaders().getId());
            LOGGER.info("ReservationDto: {}", message.getPayload());
            LOGGER.info("message.getPayload().getId(): " + message.getPayload().getId());
            LOGGER.info("message.getPayload().getStatus(): " + message.getPayload().getStatus());
            LOGGER.info("message.getPayload().getFlight().getId(): " + message.getPayload().getFlight().getId());
            LOGGER.info("message.getPayload().getFlight().getReference(): " + message.getPayload().getFlight().getReference());
            paymentService.addReservation(message.getPayload().getId(), ReservationType.FLIGHT);
        };
    }

}
