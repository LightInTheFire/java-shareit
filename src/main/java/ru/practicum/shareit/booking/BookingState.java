package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> fromString(String state) {
        BookingState bookingState = switch (state.toUpperCase()) {
            case "ALL" -> ALL;
            case "CURRENT" -> CURRENT;
            case "PAST" -> PAST;
            case "FUTURE" -> FUTURE;
            case "WAITING" -> WAITING;
            case "REJECTED" -> REJECTED;
            default -> null;
        };
        return Optional.ofNullable(bookingState);
    }
}
