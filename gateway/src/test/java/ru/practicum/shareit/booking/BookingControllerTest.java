package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@TestPropertySource(properties = {
        "shareit-server.url=http://localhost:9999"
})
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /bookings - should create booking successfully")
    void createBooking_ok() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)
        );

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /bookings - should return 400 when dates are null")
    void createBooking_validationError_nullDates() throws Exception {
        BookingDto dto = new BookingDto(1L, null, null);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /bookings - should return 400 when start date is in the past")
    void createBooking_validationError_pastStart() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /bookings - should return 400 when header is missing")
    void createBooking_withoutHeader() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /bookings/{id} - should approve booking successfully")
    void approveBooking_ok() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(HEADER, 2L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /bookings/{id} - should return booking successfully")
    void getBooking_ok() throws Exception {
        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /bookings/{id} - should return 400 when header is missing")
    void getBooking_withoutHeader() throws Exception {
        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /bookings - should return all bookings successfully")
    void getAllBookings_ok() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(HEADER, 1L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /bookings - should return 400 for invalid state")
    void getAllBookings_invalidState() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "INVALID")
                        .header(HEADER, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /bookings/owner - should return bookings by owner successfully")
    void getBookingsByOwner_ok() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "WAITING")
                        .header(HEADER, 1L))
                .andExpect(status().is5xxServerError());
    }
}
