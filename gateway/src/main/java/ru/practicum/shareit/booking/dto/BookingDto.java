package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingDto(
        long itemId,
        @FutureOrPresent @NotNull LocalDateTime start,
        @Future @NotNull LocalDateTime end
) {
}
