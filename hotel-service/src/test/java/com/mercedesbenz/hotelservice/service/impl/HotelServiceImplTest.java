package com.mercedesbenz.hotelservice.service.impl;

import com.mercedesbenz.basedomains.dto.ResponseDto;
import com.mercedesbenz.basedomains.dto.hotel.RoomReservationFiltersDto;
import com.mercedesbenz.hotelservice.entity.Availability;
import com.mercedesbenz.hotelservice.entity.Hotel;
import com.mercedesbenz.hotelservice.entity.Reservation;
import com.mercedesbenz.hotelservice.entity.Room;
import com.mercedesbenz.hotelservice.repository.AvailabilityRepository;
import com.mercedesbenz.hotelservice.repository.ReservationRepository;
import com.mercedesbenz.hotelservice.repository.RoomRepository;
import com.mercedesbenz.hotelservice.service.APIClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private APIClient apiClient;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    @Captor
    ArgumentCaptor<Availability> availabilityArgumentCaptor;

    @Captor
    ArgumentCaptor<Reservation> reservationArgumentCaptor;

    List<Hotel> hotels;
    Room room;

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
    }

    @Test
    void bookRoomReservationAtTheStartOfAvailability() {
        RoomReservationFiltersDto roomReservationFiltersDto = new RoomReservationFiltersDto();
        roomReservationFiltersDto.setStartDate(1672570800000L); // 01/01/2023 12:00:00 CET
        roomReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        roomReservationFiltersDto.setReservationID(id);

        when(roomRepository.findById(any())).thenReturn(Optional.ofNullable(room));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);

        hotelService.bookRoom(room.getId(), roomReservationFiltersDto);

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        dateCheck.set(2023, Calendar.JANUARY, 10, 12, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 31, 10, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 10, 10, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of reservation is not correct");
    }

    @Test
    void bookRoomReservationInTheMiddleOfAvailability() {
        RoomReservationFiltersDto roomReservationFiltersDto = new RoomReservationFiltersDto();
        roomReservationFiltersDto.setStartDate(1672916400000L); // 05/01/2023 12:00:00 CET
        roomReservationFiltersDto.setEndDate(1673341200000L); // 10/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        roomReservationFiltersDto.setReservationID(id);

        when(roomRepository.findById(any())).thenReturn(Optional.ofNullable(room));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);

        hotelService.bookRoom(room.getId(), roomReservationFiltersDto);

        verify(availabilityRepository, times(2)).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getAllValues());
        assertNotNull(availabilityArgumentCaptor.getAllValues().get(0));
        assertNotNull(availabilityArgumentCaptor.getAllValues().get(1));
        assertNotNull(reservationArgumentCaptor.getValue());

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        dateCheck.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getAllValues().get(0).getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 5, 10, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getAllValues().get(0).getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 10, 12, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getAllValues().get(1).getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 31, 10, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getAllValues().get(1).getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 5, 12, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));

        dateCheck.set(2023, Calendar.JANUARY, 10, 10, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR));
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH));
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE));
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND));
    }

    @Test
    void bookRoomReservationAtTheEndOfAvailability() {
        RoomReservationFiltersDto roomReservationFiltersDto = new RoomReservationFiltersDto();
        roomReservationFiltersDto.setStartDate(1673348400000L); // 10/01/2023 12:00:00 CET
        roomReservationFiltersDto.setEndDate(1675155600000L); // 31/01/2023 10:00:00 CET
        UUID id = UUID.randomUUID();
        roomReservationFiltersDto.setReservationID(id);

        when(roomRepository.findById(any())).thenReturn(Optional.ofNullable(room));
        when(availabilityRepository.save(any())).thenReturn(null);
        when(reservationRepository.save(any())).thenReturn(null);
        ResponseDto response = new ResponseDto();
        response.setData(id.toString());
        when(apiClient.bookCheckReservationID(any())).thenReturn(response);

        hotelService.bookRoom(room.getId(), roomReservationFiltersDto);

        verify(availabilityRepository).save(availabilityArgumentCaptor.capture());
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        assertNotNull(availabilityArgumentCaptor.getValue());
        assertNotNull(reservationArgumentCaptor.getValue());

        Calendar dateCheck = Calendar.getInstance();
        Calendar argumentDate = Calendar.getInstance();

        verify(availabilityRepository, times(1)).delete(any());
        verify(apiClient, times(1)).bookCheckReservationID(any());

        dateCheck.set(2023, Calendar.JANUARY, 1, 12, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 10, 10, 0, 0);
        argumentDate.setTimeInMillis(availabilityArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of availability before reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of availability before reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 10, 12, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getStartDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "Start YEAR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "Start MONTH of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "Start DAY of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "Start HOUR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "Start MINUTE of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "Start SECOND of reservation is not correct");

        dateCheck.set(2023, Calendar.JANUARY, 31, 10, 0, 0);
        argumentDate.setTimeInMillis(reservationArgumentCaptor.getValue().getEndDate());
        assertEquals(dateCheck.get(Calendar.YEAR), argumentDate.get(Calendar.YEAR), "End YEAR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MONTH), argumentDate.get(Calendar.MONTH), "End MONTH of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.DAY_OF_MONTH), argumentDate.get(Calendar.DAY_OF_MONTH), "End DAY of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.HOUR_OF_DAY), argumentDate.get(Calendar.HOUR_OF_DAY), "End HOUR of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.MINUTE), argumentDate.get(Calendar.MINUTE), "End MINUTE of reservation is not correct");
        assertEquals(dateCheck.get(Calendar.SECOND), argumentDate.get(Calendar.SECOND), "End SECOND of reservation is not correct");
    }
}
