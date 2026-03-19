package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    @DisplayName("fromString - should return ALL for 'ALL'")
    void fromString_all() {
        Optional<BookingState> state = BookingState.fromString("ALL");
        assertTrue(state.isPresent());
        assertEquals(BookingState.ALL, state.get());
    }

    @Test
    @DisplayName("fromString - should return CURRENT for 'CURRENT' (case insensitive)")
    void fromString_current_caseInsensitive() {
        Optional<BookingState> state = BookingState.fromString("current");
        assertTrue(state.isPresent());
        assertEquals(BookingState.CURRENT, state.get());
    }

    @Test
    @DisplayName("fromString - should return PAST for 'PAST'")
    void fromString_past() {
        Optional<BookingState> state = BookingState.fromString("PAST");
        assertTrue(state.isPresent());
        assertEquals(BookingState.PAST, state.get());
    }

    @Test
    @DisplayName("fromString - should return FUTURE for 'FUTURE'")
    void fromString_future() {
        Optional<BookingState> state = BookingState.fromString("FUTURE");
        assertTrue(state.isPresent());
        assertEquals(BookingState.FUTURE, state.get());
    }

    @Test
    @DisplayName("fromString - should return WAITING for 'WAITING'")
    void fromString_waiting() {
        Optional<BookingState> state = BookingState.fromString("WAITING");
        assertTrue(state.isPresent());
        assertEquals(BookingState.WAITING, state.get());
    }

    @Test
    @DisplayName("fromString - should return REJECTED for 'REJECTED'")
    void fromString_rejected() {
        Optional<BookingState> state = BookingState.fromString("REJECTED");
        assertTrue(state.isPresent());
        assertEquals(BookingState.REJECTED, state.get());
    }

    @Test
    @DisplayName("fromString - should return empty for invalid string")
    void fromString_invalid() {
        Optional<BookingState> state = BookingState.fromString("INVALID_STATE");
        assertTrue(state.isEmpty());
    }

    @Test
    @DisplayName("fromString - should handle null input")
    void fromString_null() {
        assertThrows(NullPointerException.class, () -> BookingState.fromString(null));
    }
}
