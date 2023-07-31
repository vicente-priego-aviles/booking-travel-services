package com.mercedesbenz.flightservice.service.impl;

import com.mercedesbenz.basedomains.dto.FlightDto;
import com.mercedesbenz.flightservice.entity.Flight;
import com.mercedesbenz.flightservice.repository.FlightRepository;
import com.mercedesbenz.flightservice.service.FlightService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FlightServiceImpl implements FlightService {

    private FlightRepository flightRepository;
    private ModelMapper modelMapper;
    @Override
    public List<FlightDto> insertAll(List<FlightDto> flights) {
        Iterable<Flight> flightsEntities = flights.stream().map((flight) -> modelMapper.map(flight, Flight.class)).toList();
        List<Flight> savedFlightsEntities = flightRepository.saveAll(flightsEntities);
        return savedFlightsEntities.stream().map((flight -> modelMapper.map(flight, FlightDto.class))).toList();
    }

    @Override
    public List<FlightDto> findAll() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream().map(flight -> modelMapper.map(flight, FlightDto.class)).toList();
    }

    @Override
    public FlightDto findOne(UUID id) {
        Flight flight = flightRepository.findById(id).orElse(null);
        return (flight != null ? modelMapper.map(flight, FlightDto.class) : null);
    }
}
