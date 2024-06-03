package com.company.hotelservice.service.impl;

import com.company.basedomains.dto.ResponseDto;
import com.company.basedomains.dto.Status;
import com.company.basedomains.dto.hotel.RoomReservationFiltersDto;
import com.company.hotelservice.entity.Availability;
import com.company.hotelservice.entity.Hotel;
import com.company.hotelservice.entity.Reservation;
import com.company.hotelservice.entity.Room;
import com.company.hotelservice.helpers.BookingAvailabilityHelper;
import com.company.hotelservice.helpers.dto.BookingAvailabilityDto;
import com.company.hotelservice.stream.ReservationProducer;
import com.company.hotelservice.repository.AvailabilityRepository;
import com.company.hotelservice.repository.ReservationRepository;
import com.company.hotelservice.repository.RoomRepository;
import com.company.hotelservice.service.APIClient;
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

import java.util.*;

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
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BookingAvailabilityHelper bookingAvailabilityHelper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    @Captor
    ArgumentCaptor<Availability> availabilityArgumentCaptor;

    @Captor
    ArgumentCaptor<Room> roomArgumentCaptor;

    @Captor
    ArgumentCaptor<Reservation> reservationArgumentCaptor;

    List<Hotel> hotels;
    Room room;
    Reservation reservation;
    Reservation reservationToCancel;

    @BeforeEach
    void setUp() {
        hotels = new ArrayList<>();
        Hotel hotel = new Hotel();
        hotel.setName("Hotel 1");
        hotel.setDirection("Direction 1");
        hotel.setCostPerNight(140L);
        Availability availability = new Availability();
        availability.setStartDate(1672570800000L); // 01/01/2023 12:00:00 CET
        availability.setEndDate(1675155600000L); // 31/01/2023 10:00:00 CET
        Availability availability2 = new Availability();
        availability2.setStartDate(1676458800000L); // 15/02/2023 12:00:00 CET
        availability2.setEndDate(1677322800000L); // 25/02/2023 10:00:00 CET
        List<Availability> availabilities = new ArrayList<>();
        availabilities.add(availability);
        availabilities.add(availability2);
        room = new Room();
        room.setTitle("Room 1");
        room.setPeopleCapacity(2L);
        room.setAvailabilities(availabilities);
        List<Room> rooms = new ArrayList<>();
        rooms.add(room);
        hotel.setRooms(rooms);
        hotels.add(hotel);

        reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setStatus(Status.IN_PROGRESS);
        reservation.setStartDate(1700000000000L);
        reservation.setEndDate(1750000000000L);
        reservation.setRoom(room);

        reservationToCancel = new Reservation();
        reservationToCancel.setId(UUID.randomUUID().toString());
        reservationToCancel.setRoom(room);
        reservationToCancel.setStatus(Status.IN_PROGRESS);
        reservationToCancel.setStartDate(1000000000000L);
        reservationToCancel.setEndDate(1500000000000L);
    }

    @Test
    void bookRoomReservationAtTheStartOfAvailability() {
        RoomReservationFiltersDto roomReservationFiltersDto = new RoomReservationFiltersDto();
        roomReservationFiltersDto.setStartDate(1672570800000L); // 01/01/2023 12:00:00 CET
        roomReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        roomReservationFiltersDto.setReservationID(id.toString());

        BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
        bookingAvailabilityDto.setAvailabilityBeforeReservation(null);
        bookingAvailabilityDto.setAvailabilityAfterReservation(new Availability(null, 1676026800000L, 1675155600000L, room));
        bookingAvailabilityDto.setAvailabilityBookable(new Availability(null, 1672570800000L, 1673341200000L, room));
        List<Availability> availabilitiesToSaveWithRoom = new ArrayList<>();
        availabilitiesToSaveWithRoom.add(new Availability(null, 1676458800000L, 1677322800000L, room));
        bookingAvailabilityDto.setAvailabilitiesToSaveWithRoom(availabilitiesToSaveWithRoom);

        when(roomRepository.findById(any())).thenReturn(Optional.ofNullable(room));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        doNothing().when(reservationProducer).send(any());
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);
        when(bookingAvailabilityHelper.calculateAvailabilities(any(), any())).thenReturn(bookingAvailabilityDto);

        hotelService.bookRoom(room.getId(), roomReservationFiltersDto);

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        assertEquals(roomReservationFiltersDto.getReservationID(), reservationArgumentCaptor.getValue().getId(), "Reservation ID saved is not correct");
        assertEquals(roomReservationFiltersDto.getStartDate(), reservationArgumentCaptor.getValue().getStartDate(), "Reservation startDate is not correct");
        assertEquals(roomReservationFiltersDto.getEndDate(), reservationArgumentCaptor.getValue().getEndDate(), "Reservation endDate is not correct");
        assertEquals(Status.IN_PROGRESS, reservationArgumentCaptor.getValue().getStatus(), "Reservation status should be IN_PROGRESS");
    }

    @Test
    void bookRoomReservationInTheMiddleOfAvailability() {
        RoomReservationFiltersDto roomReservationFiltersDto = new RoomReservationFiltersDto();
        roomReservationFiltersDto.setStartDate(1672916400000L); // 05/01/2023 12:00:00 CET
        roomReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        roomReservationFiltersDto.setReservationID(id.toString());

        BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
        bookingAvailabilityDto.setAvailabilityBeforeReservation(new Availability(null, 1672570800000L, 1672909200000L, room));
        bookingAvailabilityDto.setAvailabilityAfterReservation(new Availability(null, 1673348400000L, 1675069200000L, room));
        bookingAvailabilityDto.setAvailabilityBookable(new Availability(null, 1672916400000L, 1673341200000L, room));
        List<Availability> availabilitiesToSaveWithRoom = new ArrayList<>();
        availabilitiesToSaveWithRoom.add(new Availability(null, 1676458800000L, 1677322800000L, room));
        bookingAvailabilityDto.setAvailabilitiesToSaveWithRoom(availabilitiesToSaveWithRoom);

        when(roomRepository.findById(any())).thenReturn(Optional.ofNullable(room));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        doNothing().when(reservationProducer).send(any());
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);
        when(bookingAvailabilityHelper.calculateAvailabilities(any(), any())).thenReturn(bookingAvailabilityDto);

        hotelService.bookRoom(room.getId(), roomReservationFiltersDto);

        verify(availabilityRepository, times(2)).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getAllValues());
        assertNotNull(reservationArgumentCaptor.getValue());

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        assertEquals(roomReservationFiltersDto.getReservationID(), reservationArgumentCaptor.getValue().getId(), "Reservation ID is not correct");
        assertEquals(roomReservationFiltersDto.getStartDate(), reservationArgumentCaptor.getValue().getStartDate(), "Reservation startDate is not correct");
        assertEquals(roomReservationFiltersDto.getEndDate(), reservationArgumentCaptor.getValue().getEndDate(), "Reservation endDate is not correct");
        assertEquals(Status.IN_PROGRESS, reservationArgumentCaptor.getValue().getStatus(), "Reservation status should be IN_PROGRESS");
    }

    @Test
    void bookRoomReservationAtTheEndOfAvailability() {
        RoomReservationFiltersDto roomReservationFiltersDto = new RoomReservationFiltersDto();
        roomReservationFiltersDto.setStartDate(1673348400000L); // 10/01/2023 12:00:00 CET
        roomReservationFiltersDto.setEndDate(1675155600000L); // 31/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        roomReservationFiltersDto.setReservationID(id.toString());

        BookingAvailabilityDto bookingAvailabilityDto = new BookingAvailabilityDto();
        bookingAvailabilityDto.setAvailabilityBeforeReservation(new Availability(null, 1672570800000L, 1673341200000L, room));
        bookingAvailabilityDto.setAvailabilityAfterReservation(null);
        bookingAvailabilityDto.setAvailabilityBookable(new Availability(null, 1673348400000L, 1675155600000L, room));
        List<Availability> availabilitiesToSaveWithRoom = new ArrayList<>();
        availabilitiesToSaveWithRoom.add(new Availability(null, 1676458800000L, 1677322800000L, room));
        bookingAvailabilityDto.setAvailabilitiesToSaveWithRoom(availabilitiesToSaveWithRoom);

        when(roomRepository.findById(any())).thenReturn(Optional.ofNullable(room));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        doNothing().when(reservationProducer).send(any());
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);
        when(bookingAvailabilityHelper.calculateAvailabilities(any(), any())).thenReturn(bookingAvailabilityDto);

        hotelService.bookRoom(room.getId(), roomReservationFiltersDto);

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        assertEquals(roomReservationFiltersDto.getReservationID(), reservationArgumentCaptor.getValue().getId(), "Reservation ID saved is not correct");
        assertEquals(roomReservationFiltersDto.getStartDate(), reservationArgumentCaptor.getValue().getStartDate(), "Reservation startDate is not correct");
        assertEquals(roomReservationFiltersDto.getEndDate(), reservationArgumentCaptor.getValue().getEndDate(), "Reservation endDate is not correct");
        assertEquals(Status.IN_PROGRESS, reservationArgumentCaptor.getValue().getStatus(), "Reservation status should be IN_PROGRESS");
    }

    @Test
    void cancelReservation() {
        when(reservationRepository.findById(any())).thenReturn(Optional.ofNullable(reservationToCancel));
        when(roomRepository.findById(any())).thenReturn(Optional.ofNullable(room));

        hotelService.cancelReservation(UUID.randomUUID().toString());

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        verify(availabilityRepository, times(1)).save(any());
        verify(roomRepository, times(1)).save(any());
        verify(reservationRepository, times(1)).save(any());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        assertEquals(1000000000000L, availabilityArgumentCaptor.getValue().getStartDate(), "The startDate is not matching.");
        assertEquals(1500000000000L, availabilityArgumentCaptor.getValue().getEndDate(), "The endDate is not matching.");
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
