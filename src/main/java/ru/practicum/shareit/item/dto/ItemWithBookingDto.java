package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

public record ItemWithBookingDto(
        Long id,
        String name,
        String description,
        boolean available,
        BookingInfoDto lastBooking,
        BookingInfoDto nextBooking,
        List<CommentDto> comments
) {
}
