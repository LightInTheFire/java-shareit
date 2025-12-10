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
}
