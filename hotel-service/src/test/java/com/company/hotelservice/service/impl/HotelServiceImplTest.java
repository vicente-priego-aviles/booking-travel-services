package com.company.hotelservice.service.impl;

import com.company.basedomains.dto.ResponseDto;
import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.hotel.RoomReservationFiltersDto;
import com.company.hotelservice.entity.Hotel;
import com.company.hotelservice.entity.Reservation;
import com.company.hotelservice.repository.HotelRepository;
import com.company.hotelservice.repository.ReservationRepository;
import com.company.hotelservice.service.APIClient;
import com.company.hotelservice.stream.ReservationProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceImplTest {

    @Mock
    private ReservationProducer reservationProducer;

    @Mock
    private APIClient apiClient;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    @Captor
    ArgumentCaptor<Reservation> reservationArgumentCaptor;

    List<Hotel> hotels;
    Hotel hotel;
    Reservation reservation;
    Reservation reservationToCancel;

    @BeforeEach
    void setUp() {
        hotels = new ArrayList<>();
        hotel = new Hotel();
        hotel.setName("Hotel 1");
        hotel.setDirection("Direction 1");
        hotel.setCostPerNight(140L);
        hotels.add(hotel);

        reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setStatus(Status.IN_PROGRESS);
        reservation.setHotel(hotel);

        reservationToCancel = new Reservation();
        reservationToCancel.setId(UUID.randomUUID().toString());

    }

    @Test
    void bookRoom() {
        RoomReservationFiltersDto roomReservationFiltersDto = new RoomReservationFiltersDto();
        UUID id = UUID.randomUUID();
        roomReservationFiltersDto.setReservationID(id.toString());

        when(reservationRepository.save(any())).thenReturn(null);
        doNothing().when(reservationProducer).send(any());
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);

        hotelService.bookHotel(hotel.getId(), roomReservationFiltersDto);

        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(reservationArgumentCaptor.getValue());

        verify(apiClient, times(1)).bookCheckReservationID(any());

        assertEquals(roomReservationFiltersDto.getReservationID(), reservationArgumentCaptor.getValue().getId(), "Reservation ID saved is not correct");
        assertEquals(Status.IN_PROGRESS, reservationArgumentCaptor.getValue().getStatus(), "Reservation status should be IN_PROGRESS");
    }

    @Test
    void cancelReservation() {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservationToCancel));
        when(hotelRepository.findById(any())).thenReturn(Optional.ofNullable(hotel));

        hotelService.cancelReservation(UUID.randomUUID().toString());

        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        verify(hotelRepository, times(1)).save(any());
        verify(reservationRepository, times(1)).save(any());

        assertNotNull(reservationArgumentCaptor.getValue());

        assertEquals(Status.CANCELLED, reservationArgumentCaptor.getValue().getStatus(), "The status should be CANCELLED and it is not.");
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    void updateReservationStatus(Status status) {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservation));

        hotelService.updateReservationStatus(UUID.randomUUID().toString(), status);

        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        verify(reservationRepository, times(1)).save(any());

        assertNotNull(reservationArgumentCaptor.getValue());

        assertEquals(status, reservationArgumentCaptor.getValue().getStatus(), "The status should be PAID but it is not.");
    }
}
