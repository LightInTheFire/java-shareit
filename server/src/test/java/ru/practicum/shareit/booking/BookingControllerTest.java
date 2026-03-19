package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    @DisplayName("Create a new booking via POST /bookings")
    void createBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                bookingDto.start(),
                bookingDto.end(),
                new ItemDto(1L, "ItemName", "ItemDescription", true),
                new UserDto(2L, "John", "mail@mail"),
                BookingStatus.WAITING
        );

        Mockito.when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(2L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("Approve a booking via PATCH /bookings/{id}")
    void approveBookingTest() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                new ItemDto(1L, "ItemName", "ItemDescription", true),
                new UserDto(2L, "John", "mail@mail"),
                BookingStatus.APPROVED
        );

        Mockito.when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{id}", 1)
                        .param("approved", "true")
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Get booking by ID via GET /bookings/{id}")
    void getBookingByIdTest() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                new ItemDto(1L, "ItemName", "ItemDescription", true),
                new UserDto(2L, "John", "mail@mail"),
                BookingStatus.WAITING
        );

        Mockito.when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/{id}", 1)
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(2L));
    }

    @Test
    @DisplayName("Get all bookings of user via GET /bookings")
    void getAllBookingsOfUserTest() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                new ItemDto(1L, "ItemName", "ItemDescription", true),
                new UserDto(2L, "John", "email@email"),
                BookingStatus.WAITING
        );

        Mockito.when(bookingService.getAllBookingsOfUser(anyLong(), any()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(2L));
    }

    @Test
    @DisplayName("Get all bookings by owner via GET /bookings/owner")
    void getBookingsByOwnerTest() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                new ItemDto(1L, "ItemName", "ItemDescription", true),
                new UserDto(2L, "John", "email@email"),
                BookingStatus.WAITING
        );

        Mockito.when(bookingService.getAllBookingsByOwner(anyLong(), any()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(2L));
    }
}
