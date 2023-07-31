package com.mercedesbenz.flightservice.service;

import com.mercedesbenz.basedomains.dto.FlightDto;

import java.util.List;
import java.util.UUID;

public interface FlightService {
    public List<FlightDto> insertAll(List<FlightDto> flights);
    public List<FlightDto> findAll();
    public FlightDto findOne(UUID id);
}
