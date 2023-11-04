package com.mercedesbenz.paymentservice.stream;

import com.mercedesbenz.basedomains.dto.hotel.ReservationDto;
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
public class HotelReservationConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(HotelReservationConsumer.class);

    @Autowired
    private PaymentService paymentService;

    @Bean
    Consumer<Message<ReservationDto>> inputHotel() {
        return message -> {
            LOGGER.info("Headers: {}", message.getHeaders());
            LOGGER.info("Headers.Id: {}", message.getHeaders().getId());
            LOGGER.info("ReservationDto: {}", message.getPayload());
            LOGGER.info("message.getPayload().getId(): " + message.getPayload().getId());
            LOGGER.info("message.getPayload().getStatus(): " + message.getPayload().getStatus());
            LOGGER.info("message.getPayload().getStartDate(): " + message.getPayload().getStartDate());
            LOGGER.info("message.getPayload().getEndDate(): " + message.getPayload().getEndDate());
            LOGGER.info("message.getPayload().getCar().getId(): " + message.getPayload().getRoom().getId());
            paymentService.addReservation(message.getPayload().getId(), ReservationType.HOTEL);
        };
    }

}
