package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class BookingMapper {
    public BookingResponseDto toBookingResponseDto(Booking booking) {
        Long bookingId = booking.getId();
        LocalDateTime bookingStartTime = booking.getStartTime();
        LocalDateTime bookingEndTime = booking.getEndTime();
        Long itemId = booking.getItem().getId();
        String itemName = booking.getItem().getName();
        String itemDescription = booking.getItem().getDescription();
        boolean itemAvailable = booking.getItem().isAvailable();
        ItemDto itemDto = new ItemDto(
                itemId,
                itemName,
                itemDescription,
                itemAvailable
        );
        Long userId = booking.getBooker().getId();
        String userName = booking.getBooker().getName();
        String userEmail = booking.getBooker().getEmail();
        UserDto userDto = new UserDto(userId, userName, userEmail);
        BookingStatus bookingStatus = booking.getStatus();
        return new BookingResponseDto(
                bookingId,
                bookingStartTime,
                bookingEndTime,
                itemDto,
                userDto,
                bookingStatus
        );
    }

    public static Booking fromDto(BookingDto bookingDto, User booker, Item item) {
        return new Booking(null,
                bookingDto.start(),
                bookingDto.end(),
                item,
                booker,
                BookingStatus.WAITING);
    }
}
