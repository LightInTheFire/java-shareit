package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;
    private User owner;
    private Item item;

    @BeforeAll
    void setup() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Electric drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    @DisplayName("Should create a new booking successfully")
    void testCreateBooking() {
        BookingDto bookingDto = new BookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        BookingResponseDto response = bookingService.createBooking(booker.getId(), bookingDto);

        assertNotNull(response.id());
        assertEquals(item.getId(), response.item().id());
        assertEquals(booker.getId(), response.booker().id());
        assertEquals(BookingStatus.WAITING, response.status());
    }

    @Test
    @DisplayName("Should approve a booking by item owner")
    void testApproveBooking() {
        BookingDto bookingDto = new BookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        BookingResponseDto booking = bookingService.createBooking(booker.getId(), bookingDto);

        BookingResponseDto approvedBooking = bookingService.approveBooking(booking.id(), true, owner.getId());

        assertEquals(BookingStatus.APPROVED, approvedBooking.status());
    }

    @Test
    @DisplayName("Should retrieve all bookings of a user")
    void testGetAllBookingsOfUser() {
        bookingService.createBooking(booker.getId(), new BookingDto(
                item.getId(),
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2)
        ));
        bookingService.createBooking(booker.getId(), new BookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        ));

        List<BookingResponseDto> allBookings = bookingService.getAllBookingsOfUser(booker.getId(), BookingState.ALL);

        assertFalse(allBookings.isEmpty());
        assertTrue(allBookings.stream().anyMatch(b -> b.booker().id().equals(booker.getId())));
    }
}
