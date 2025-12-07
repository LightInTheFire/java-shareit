package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record BookingInfoDto(
        Long id,
        Long bookerId,
        LocalDateTime start,
        LocalDateTime end
) {
}
