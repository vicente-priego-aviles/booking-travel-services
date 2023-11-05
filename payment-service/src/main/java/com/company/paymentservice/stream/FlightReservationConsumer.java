package com.company.paymentservice.stream;

import com.company.basedomains.dto.flight.ReservationDto;
import com.company.paymentservice.helpers.ReservationType;
import com.company.paymentservice.service.PaymentService;
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
            LOGGER.info("{} - Headers: {}", this.getClass().getName(), message.getHeaders());
            LOGGER.info("{} - Headers.Id: {}", this.getClass().getName(), message.getHeaders().getId());
            LOGGER.info("{} - ReservationDto: {}", this.getClass().getName(), message.getPayload());
            LOGGER.info("{} - message.getPayload().getId(): {}", this.getClass().getName(), message.getPayload().getId());
            LOGGER.info("{} - message.getPayload().getStatus(): {}", this.getClass().getName(), message.getPayload().getStatus());
            LOGGER.info("{} - message.getPayload().getFlight().getId(): {}", this.getClass().getName(), message.getPayload().getFlight().getId());
            LOGGER.info("{} - message.getPayload().getFlight().getReference(): {}", this.getClass().getName(), message.getPayload().getFlight().getReference());
            paymentService.addReservation(message.getPayload().getId(), ReservationType.FLIGHT);
        };
    }

}
