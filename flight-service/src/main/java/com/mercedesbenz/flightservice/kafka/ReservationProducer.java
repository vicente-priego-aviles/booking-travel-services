package com.mercedesbenz.flightservice.kafka;

import com.mercedesbenz.basedomains.dto.flight.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationProducer {

    private final Logger LOGGER = LoggerFactory.getLogger(ReservationProducer.class);

    @Value("${topic.name.producer}")
    private String topicName;

    private final KafkaTemplate<String, ReservationDto> kafkaTemplate;

    public void send(ReservationDto reservation) {
        LOGGER.info("Reservation sent from FLIGHTS service: " + reservation.getId());
        kafkaTemplate.send(topicName, reservation.getId().toString(), reservation);
    }

}
