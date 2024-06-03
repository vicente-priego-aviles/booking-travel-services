package com.company.carservice.service;

import com.company.basedomains.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "FLIGHT-SERVICE")
public interface APIClient {
    @GetMapping("api/flights-booking/book/check/{reservation-id}")
    ResponseDto bookCheckReservationID(@PathVariable("reservation-id") String reservationID);
}
