package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import ru.practicum.shareit.booking.dto.BookingDto;

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final RestClient restClient;

    public BookingController(@Value("${shareit-server.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl.concat("/bookings"))
                .build();
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(SHARER_USER_ID_HEADER) long bookerId,
            @RequestBody @Valid BookingDto bookingDto
    ) {
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookingDto)
                .header(SHARER_USER_ID_HEADER, String.valueOf(bookerId))
                .retrieve()
                .toEntity(Object.class);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(SHARER_USER_ID_HEADER) long ownerId
    ) {
        return restClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/" + bookingId)
                        .queryParam("approved", approved)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(SHARER_USER_ID_HEADER, String.valueOf(ownerId))
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBooking(
            @PathVariable long bookingId,
            @RequestHeader(SHARER_USER_ID_HEADER) long userId
    ) {
        return restClient.get()
                .uri("/" + bookingId)
                .header(SHARER_USER_ID_HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsOfUser(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(SHARER_USER_ID_HEADER) long userId
    ) {
        BookingState bookingState = BookingState.fromString(state)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking state"));

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("state", bookingState)
                        .build())
                .header(SHARER_USER_ID_HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(SHARER_USER_ID_HEADER) long ownerId
    ) {
        BookingState bookingState = BookingState.fromString(state)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking state"));
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/owner")
                        .queryParam("state", bookingState)
                        .build())
                .header(SHARER_USER_ID_HEADER, String.valueOf(ownerId))
                .retrieve()
                .toEntity(Object.class);
    }
}
