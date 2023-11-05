package com.company.paymentservice.stream;

import com.company.basedomains.dto.hotel.ReservationDto;
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
public class HotelReservationConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(HotelReservationConsumer.class);

    @Autowired
    private PaymentService paymentService;

    @Bean
    Consumer<Message<ReservationDto>> inputHotel() {
        return message -> {
            LOGGER.info("{} - Headers: {}", this.getClass().getName(), message.getHeaders());
            LOGGER.info("{} - Headers.Id: {}", this.getClass().getName(), message.getHeaders().getId());
            LOGGER.info("{} - ReservationDto: {}", this.getClass().getName(), message.getPayload());
            LOGGER.info("{} - message.getPayload().getId(): {}", this.getClass().getName(), message.getPayload().getId());
            LOGGER.info("{} - message.getPayload().getStatus(): {}", this.getClass().getName(), message.getPayload().getStatus());
            LOGGER.info("{} - message.getPayload().getStartDate(): {}", this.getClass().getName(), message.getPayload().getStartDate());
            LOGGER.info("{} - message.getPayload().getEndDate(): {}", this.getClass().getName(), message.getPayload().getEndDate());
            LOGGER.info("{} - message.getPayload().getCar().getId(): {}", this.getClass().getName(), message.getPayload().getRoom().getId());
            paymentService.addReservation(message.getPayload().getId(), ReservationType.HOTEL);
        };
    }

}
