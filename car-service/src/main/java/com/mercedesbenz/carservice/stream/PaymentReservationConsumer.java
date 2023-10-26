package com.mercedesbenz.carservice.stream;

import com.mercedesbenz.basedomains.dto.ReservationDto;
import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.carservice.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PaymentReservationConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(PaymentReservationConsumer.class);

    @Autowired
    private CarService carService;

    @Autowired
    private SubscribableChannel inputPayments;


    @Bean
    Consumer<Message<ReservationDto>> input() {
        return message -> {
            LOGGER.info("Headers.Id: {}", message.getHeaders().getId());
            LOGGER.info("Headers: {}", message.getHeaders());
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
    /*public void input() {
        inputPayments.subscribe(message -> {
            LOGGER.info("Headers.Id: {}", message.getHeaders().getId());
            LOGGER.info("Headers: {}", message.getHeaders());
            LOGGER.info("ReservationDto: {}", message.getPayload());
            ReservationDto payload = (ReservationDto) message.getPayload();
            if (payload.getStatus() != null) {
                if (payload.getStatus() == Status.CANCELLED) {
                    cancel(payload);
                } else {
                    update(payload);
                }
            }
        });
    }*/

    private void cancel(ReservationDto reservation) {
        carService.cancelReservation(reservation.getId());
    }

    private void update(ReservationDto reservation) {
        carService.updateReservationStatus(reservation.getId(), reservation.getStatus());
    }

}
