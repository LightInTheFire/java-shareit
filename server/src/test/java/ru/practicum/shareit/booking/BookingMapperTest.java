package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    @DisplayName("toBookingResponseDto - should map Booking to BookingResponseDto correctly")
    void toBookingResponseDto_ok() {
        User user = new User(1L, "John", "john@example.com");
        Item item = new Item(2L, "Drill", "Powerful drill", true, user, null);
        Booking booking = new Booking(3L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                item,
                user,
                BookingStatus.APPROVED);

        BookingResponseDto dto = BookingMapper.toBookingResponseDto(booking);

        assertEquals(booking.getId(), dto.id());
        assertEquals(booking.getStartTime(), dto.start());
        assertEquals(booking.getEndTime(), dto.end());

        ItemDto itemDto = dto.item();
        assertEquals(item.getId(), itemDto.id());
        assertEquals(item.getName(), itemDto.name());
        assertEquals(item.getDescription(), itemDto.description());
        assertEquals(item.isAvailable(), itemDto.available());

        UserDto userDto = dto.booker();
        assertEquals(user.getId(), userDto.id());
        assertEquals(user.getName(), userDto.name());
        assertEquals(user.getEmail(), userDto.email());

        assertEquals(booking.getStatus(), dto.status());
    }

    @Test
    @DisplayName("fromDto - should create Booking from BookingDto")
    void fromDto_ok() {
        User user = new User(1L, "John", "john@example.com");
        Item item = new Item(2L, "Drill", "Powerful drill", true, user, null);
        BookingDto bookingDto = new BookingDto(2L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1));

        Booking booking = BookingMapper.fromDto(bookingDto, user, item);

        assertNull(booking.getId());
        assertEquals(bookingDto.start(), booking.getStartTime());
        assertEquals(bookingDto.end(), booking.getEndTime());
        assertEquals(user, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    @DisplayName("toBookingInfoDto - should map Booking to BookingInfoDto correctly")
    void toBookingInfoDto_ok() {
        User user = new User(1L, "John", "john@example.com");
        Item item = new Item(2L, "Drill", "Powerful drill", true, user, null);
        Booking booking = new Booking(3L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                item,
                user,
                BookingStatus.APPROVED);

        Optional<BookingInfoDto> optDto = BookingMapper.toBookingInfoDto(booking);

        assertTrue(optDto.isPresent());
        BookingInfoDto dto = optDto.get();
        assertEquals(booking.getId(), dto.id());
        assertEquals(user.getId(), dto.bookerId());
        assertEquals(booking.getStartTime(), dto.start());
        assertEquals(booking.getEndTime(), dto.end());
    }

    @Test
    @DisplayName("toBookingInfoDto - should return empty for null input")
    void toBookingInfoDto_null() {
        Optional<BookingInfoDto> optDto = BookingMapper.toBookingInfoDto(null);
        assertTrue(optDto.isEmpty());
    }
}
