package com.company.hotelservice.stream;

import com.company.basedomains.dto.ReservationDto;
import com.company.basedomains.dto.Status;
import com.company.hotelservice.service.HotelService;
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
    private HotelService hotelService;

    @Bean
    Consumer<Message<ReservationDto>> input() {
        return message -> {
            LOGGER.info("Headers: {}", message.getHeaders());
            LOGGER.info("Headers.Id: {}", message.getHeaders().getId());
            LOGGER.info("ReservationDto: {}", message.getPayload());
            LOGGER.info("message.getPayload().getId(): " + message.getPayload().getId());
            LOGGER.info("message.getPayload().getStatus(): " + message.getPayload().getStatus());
            LOGGER.info("message.getPayload().isCarBooked(): " + message.getPayload().isCarBooked());
            LOGGER.info("message.getPayload().isFlightBooked(): " + message.getPayload().isFlightBooked());
            LOGGER.info("message.getPayload().isHotelBooked(): " + message.getPayload().isHotelBooked());
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
        hotelService.cancelReservation(reservation.getId());
    }

    private void update(ReservationDto reservation) {
        hotelService.updateReservationStatus(reservation.getId(), reservation.getStatus());
    }

}
