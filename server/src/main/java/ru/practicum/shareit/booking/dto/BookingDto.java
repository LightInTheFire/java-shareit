package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record BookingDto(
        long itemId,
        LocalDateTime start,
        LocalDateTime end
) {
}
