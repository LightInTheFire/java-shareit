package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booker = new User(1L, "Booker", "booker@example.com");
        owner = new User(2L, "Owner", "owner@example.com");
        item = new Item(1L, "Drill", "Powerful drill", true, owner, null);

        bookingDto = new BookingDto(
                item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
    }

    @Test
    @DisplayName("createBooking - should create booking successfully")
    void createBooking_ok() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findApprovedIntersectingBookings(anyLong(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(10L);
            return b;
        });

        BookingResponseDto response = bookingService.createBooking(booker.getId(), bookingDto);

        assertNotNull(response);
        assertEquals(10L, response.id());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("createBooking - should throw ItemUnavailableException when item is unavailable")
    void createBooking_itemUnavailable() {
        item.setAvailable(false);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ItemUnavailableException.class, () ->
                bookingService.createBooking(booker.getId(), bookingDto));
    }

    @Test
    @DisplayName("createBooking - should throw BookingIntersectionException when dates intersect")
    void createBooking_dateIntersection() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findApprovedIntersectingBookings(anyLong(), any(), any()))
                .thenReturn(List.of(new Booking()));

        assertThrows(BookingIntersectionException.class, () ->
                bookingService.createBooking(booker.getId(), bookingDto));
    }

    @Test
    @DisplayName("approveBooking - should approve booking successfully")
    void approveBooking_ok() {
        Booking booking = new Booking(1L, bookingDto.start(), bookingDto.end(), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto response = bookingService.approveBooking(booking.getId(), true, owner.getId());

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        assertNotNull(response);
    }

    @Test
    @DisplayName("approveBooking - should throw ForbiddenAccessException when non-owner approves")
    void approveBooking_notOwner() {
        Booking booking = new Booking(1L, bookingDto.start(), bookingDto.end(), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenAccessException.class, () ->
                bookingService.approveBooking(booking.getId(), true, booker.getId()));
    }

    @Test
    @DisplayName("getBookingById - should return booking for booker")
    void getBookingById_ok() {
        Booking booking = new Booking(1L, bookingDto.start(), bookingDto.end(), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));

        BookingResponseDto response = bookingService.getBookingById(booking.getId(), booker.getId());

        assertNotNull(response);
        assertEquals(booking.getId(), response.id());
    }

    @Test
    @DisplayName("getBookingById - should throw ForbiddenAccessException when user is neither owner nor booker")
    void getBookingById_noAccess() {
        User other = new User(3L, "Other", "other@example.com");
        Booking booking = new Booking(1L, bookingDto.start(), bookingDto.end(), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(other.getId())).thenReturn(Optional.of(other));

        assertThrows(ForbiddenAccessException.class, () ->
                bookingService.getBookingById(booking.getId(), other.getId()));
    }

    @Test
    @DisplayName("getAllBookingsOfUser - ALL returns bookings")
    void getAllBookingsOfUser_all() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartTimeDesc(booker.getId()))
                .thenReturn(List.of(new Booking(1L, bookingDto.start(), bookingDto.end(), item, booker, BookingStatus.WAITING)));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsOfUser(booker.getId(), BookingState.ALL);

        assertEquals(1, bookings.size());
        verify(bookingRepository).findByBookerIdOrderByStartTimeDesc(booker.getId());
    }

    @Test
    @DisplayName("getAllBookingsOfUser - FUTURE returns bookings")
    void getAllBookingsOfUser_future() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartTimeAfterOrderByStartTimeDesc(eq(booker.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking(2L, bookingDto.start().plusDays(1), bookingDto.end().plusDays(1), item, booker, BookingStatus.WAITING)));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsOfUser(booker.getId(), BookingState.FUTURE);

        assertEquals(1, bookings.size());
        verify(bookingRepository).findByBookerIdAndStartTimeAfterOrderByStartTimeDesc(eq(booker.getId()), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getAllBookingsByOwner - PAST returns bookings")
    void getAllBookingsByOwner_past() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(eq(owner.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking(3L, bookingDto.start().minusDays(2), bookingDto.end().minusDays(1), item, booker, BookingStatus.APPROVED)));

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwner(owner.getId(), BookingState.PAST);

        assertEquals(1, bookings.size());
        verify(bookingRepository).findByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(eq(owner.getId()), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getAllBookingsByOwner - empty list when no bookings")
    void getAllBookingsByOwner_empty() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartTimeDesc(owner.getId())).thenReturn(List.of());

        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwner(owner.getId(), BookingState.ALL);

        assertTrue(bookings.isEmpty());
    }

    @Test
    @DisplayName("getAllBookingsOfUser - throws NotFoundException if user not found")
    void getAllBookingsOfUser_userNotFound() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsOfUser(booker.getId(), BookingState.ALL));
    }

    @Test
    @DisplayName("getAllBookingsByOwner - throws NotFoundException if user not found")
    void getAllBookingsByOwner_userNotFound() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByOwner(owner.getId(), BookingState.ALL));
    }
}
