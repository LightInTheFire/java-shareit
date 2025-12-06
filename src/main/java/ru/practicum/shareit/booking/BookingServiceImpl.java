package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(long bookerId, BookingDto bookingDto) {
        User booker = getUserOrThrow(bookerId);
        Item item = getItemOrThrow(bookingDto.itemId());

        if (!item.isAvailable()) {
            throw new ItemUnavailableException("Item with id %d is not available for booking");
        }

        checkBookingDates(bookingDto.start(), bookingDto.end());

        Booking booking = BookingMapper.fromDto(bookingDto, booker, item);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(long bookingId, boolean approved, long ownerId) {
        Booking booking = getBookingOrThrow(bookingId);
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ForbiddenAccessException("Booking can only be approved by owner of item");
        }

        getUserOrThrow(ownerId);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(saved);
    }

    @Override
    public BookingResponseDto getBookingById(long bookingId, long userId) {
        Booking booking = getBookingOrThrow(bookingId);
        getUserOrThrow(userId);

        if (booking.getItem().getOwner().getId() != userId
                && booking.getBooker().getId() != userId) {
            throw new ForbiddenAccessException("No access to booking");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfUser(long userId, BookingState bookingState) {
        getUserOrThrow(userId);

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartTimeDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByBooker(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(
                    userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findByBookerIdAndStartTimeAfterOrderByStartTimeDesc(
                    userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartTimeDesc(
                    userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartTimeDesc(
                    userId, BookingStatus.REJECTED);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByOwner(long ownerId, BookingState bookingState) {
        getUserOrThrow(ownerId);

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartTimeDesc(ownerId);
            case CURRENT -> bookingRepository.findCurrentByOwner(ownerId, LocalDateTime.now());
            case PAST -> bookingRepository.findByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(
                    ownerId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartTimeAfterOrderByStartTimeDesc(
                    ownerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartTimeDesc(
                    ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartTimeDesc(
                    ownerId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .toList();
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(NotFoundException.supplier("User with id:%d not found", userId));
    }

    private Item getItemOrThrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(NotFoundException.supplier("Item with id:%d not found", itemId));
    }

    private Booking getBookingOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(NotFoundException.supplier("Booking with id:%d not found", bookingId));
    }

    private void checkBookingDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start booking date is after end date");
        }

        if (start.equals(end)) {
            throw new IllegalArgumentException("Booking start and end date must not be the same");
        }
    }
}
