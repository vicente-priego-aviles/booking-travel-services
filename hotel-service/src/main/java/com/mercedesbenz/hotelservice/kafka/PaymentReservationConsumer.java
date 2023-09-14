package com.mercedesbenz.hotelservice.kafka;

import com.mercedesbenz.basedomains.dto.ReservationDto;
import com.mercedesbenz.basedomains.dto.Status;
import com.mercedesbenz.hotelservice.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentReservationConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentReservationConsumer.class);

    private final HotelService hotelService;

    @Value("${topic.name.consumer.payments}")
    private String topicName;

    @KafkaListener(topics = "${topic.name.consumer.payments}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, ReservationDto> payload) {
        LOGGER.info("key: {}", payload.key());
        LOGGER.info("Headers: {}", payload.headers());
        LOGGER.info("Partition: {}", payload.partition());
        LOGGER.info("ReservationDto: {}", payload.value());
        if (payload.value() != null && payload.value().getStatus() != null) {
            if (payload.value().getStatus() == Status.CANCELLED) {
                cancel(payload.value());
            } else {
                update(payload.value());
            }
        }
    }

    private void cancel(ReservationDto reservation) {
        hotelService.cancelReservation(reservation.getId());
    }

    private void update(ReservationDto reservation) {
        hotelService.updateReservationStatus(reservation.getId(), reservation.getStatus());
    }
}
