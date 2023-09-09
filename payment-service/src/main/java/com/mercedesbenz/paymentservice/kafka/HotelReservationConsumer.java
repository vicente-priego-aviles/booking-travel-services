package com.mercedesbenz.paymentservice.kafka;

import com.mercedesbenz.basedomains.dto.flight.ReservationDto;
import com.mercedesbenz.paymentservice.helpers.ReservationType;
import com.mercedesbenz.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelReservationConsumer {

    private PaymentService paymentService;

    private final Logger LOGGER = LoggerFactory.getLogger(HotelReservationConsumer.class);

    @Value("${topic.name.consumer.hotels}")
    private String topicName;

    @KafkaListener(topics = "{topic.name.consumer.hotels}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, ReservationDto> payload) {
        LOGGER.info("key: {}", payload.key());
        LOGGER.info("Headers: {}", payload.headers());
        LOGGER.info("Partition: {}", payload.partition());
        LOGGER.info("ReservationDto: {}", payload.value());
        paymentService.addFlightReservation(payload.value().getId(), ReservationType.HOTEL);
    }
}
