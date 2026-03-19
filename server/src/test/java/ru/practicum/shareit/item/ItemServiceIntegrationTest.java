package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Item item;

    @BeforeAll
    void setup() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Drill");
        item.setDescription("Electric drill");
        item.setAvailable(true);
        item.setOwner(user);
        item = itemRepository.save(item);
    }

    @Test
    @DisplayName("Should save a new item successfully")
    void testSaveItem() {
        NewItemDto newItemDto = new NewItemDto("Hammer", "Heavy hammer", true, null);
        ItemDto savedItem = itemService.saveItem(user.getId(), newItemDto);

        assertNotNull(savedItem.id());
        assertEquals("Hammer", savedItem.name());
        assertEquals("Heavy hammer", savedItem.description());
        assertTrue(savedItem.available());

        Item itemFromDb = itemRepository.findById(savedItem.id()).orElseThrow();
        assertEquals("Hammer", itemFromDb.getName());
    }

    @Test
    @DisplayName("Should retrieve all items of a user")
    void testGetAllItemsOfUser() {
        Collection<ItemWithBookingDto> items = itemService.getAllItemsOfUser(user.getId());

        assertFalse(items.isEmpty());
        ItemWithBookingDto itemDto = items.iterator().next();
        assertEquals(item.getId(), itemDto.id());
        assertEquals(item.getName(), itemDto.name());
        assertEquals(item.getDescription(), itemDto.description());
    }

    @Test
    @DisplayName("Should create a comment for an item after booking")
    void testCreateComment() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStartTime(LocalDateTime.now().minusDays(2));
        booking.setEndTime(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        NewCommentDto newComment = new NewCommentDto("Great tool!");
        CommentDto commentDto = itemService.createComment(user.getId(), item.getId(), newComment);

        assertNotNull(commentDto.id());
        assertEquals("Great tool!", commentDto.text());
        assertEquals(user.getName(), commentDto.authorName());
        assertFalse(commentDto.created().isAfter(LocalDateTime.now()));

        List<Comment> comments = commentRepository.findAllByItem_IdInOrderByCreatedAtDesc(List.of(item.getId()));
        assertTrue(comments.stream().anyMatch(c -> c.getText().equals("Great tool!")));
    }
}
