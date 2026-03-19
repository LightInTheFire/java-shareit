package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
            @RequestHeader(SHARER_USER_ID_HEADER) long bookerId,
            @RequestBody BookingDto bookingDto
    ) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingResponseDto approveBooking(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(SHARER_USER_ID_HEADER) long ownerId
    ) {
        return bookingService.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("{bookingId}")
    public BookingResponseDto getBooking(
            @PathVariable long bookingId,
            @RequestHeader(SHARER_USER_ID_HEADER) long userId
    ) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsOfUser(
            @RequestParam BookingState state,
            @RequestHeader(SHARER_USER_ID_HEADER) long userId
    ) {

        return bookingService.getAllBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(
            @RequestParam BookingState state,
            @RequestHeader(SHARER_USER_ID_HEADER) long ownerId
    ) {
        return bookingService.getAllBookingsByOwner(ownerId, state);
    }
}
