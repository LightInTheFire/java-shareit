package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(long bookerId, BookingDto bookingDto);

    BookingResponseDto approveBooking(long bookingId, boolean approved, long ownerId);

    BookingResponseDto getBookingById(long bookingId, long userId);

    List<BookingResponseDto> getAllBookingsOfUser(long userId, BookingState bookingState);

    List<BookingResponseDto> getAllBookingsByOwner(long ownerId, BookingState bookingState);
}
