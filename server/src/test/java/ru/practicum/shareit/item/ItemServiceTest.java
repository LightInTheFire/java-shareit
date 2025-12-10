package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.ItemCommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private NewItemDto newItemDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        owner = new User(1L, "Owner", "owner@example.com");
        item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        newItemDto = new NewItemDto("Hammer", "Heavy hammer", true, null);
    }

    @Test
    @DisplayName("saveItem - should save item successfully without request")
    void saveItem_ok() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenAnswer(invocation -> {
            Item i = invocation.getArgument(0);
            i.setId(10L);
            return i;
        });

        ItemDto saved = itemService.saveItem(owner.getId(), newItemDto);

        assertNotNull(saved);
        assertEquals(10L, saved.id());
        assertEquals("Hammer", saved.name());
        verify(itemRepository).save(any());
    }

    @Test
    @DisplayName("saveItem - should throw NotFoundException when user not found")
    void saveItem_userNotFound() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.saveItem(owner.getId(), newItemDto));
    }

    @Test
    @DisplayName("updateItem - should update item successfully")
    void updateItem_ok() {
        UpdateItemDto updateDto = new UpdateItemDto("Updated Drill", null, null);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto updated = itemService.updateItem(owner.getId(), item.getId(), updateDto);

        assertEquals("Updated Drill", updated.name());
        verify(itemRepository).save(any());
    }

    @Test
    @DisplayName("updateItem - should throw ForbiddenAccessException for non-owner")
    void updateItem_notOwner() {
        UpdateItemDto updateDto = new UpdateItemDto("Updated Drill", null, null);
        User other = new User(2L, "Other", "other@example.com");
        item.setOwner(owner);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ForbiddenAccessException.class, () -> itemService.updateItem(other.getId(), item.getId(), updateDto));
    }

    @Test
    @DisplayName("createComment - should create comment successfully")
    void createComment_ok() {
        User author = new User(2L, "Author", "author@example.com");
        NewCommentDto newComment = new NewCommentDto("Great item!");
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, author, BookingStatus.APPROVED);
        Comment savedComment = new Comment(1L, "Great item!", item, author, LocalDateTime.now());

        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndTimeIsBefore(
                anyLong(), anyLong(), any(), any()
        )).thenReturn(Optional.of(booking));
        when(commentRepository.save(any())).thenReturn(savedComment);

        CommentDto dto = itemService.createComment(author.getId(), item.getId(), newComment);

        assertNotNull(dto);
        assertEquals("Great item!", dto.text());
        verify(commentRepository).save(any());
    }

    @Test
    @DisplayName("createComment - should throw ItemCommentException when user has not booked")
    void createComment_notBooked() {
        User author = new User(2L, "Author", "author@example.com");
        NewCommentDto newComment = new NewCommentDto("Great item!");

        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndTimeIsBefore(
                anyLong(), anyLong(), any(), any()
        )).thenReturn(Optional.empty());

        assertThrows(ItemCommentException.class,
                () -> itemService.createComment(author.getId(), item.getId(), newComment));
    }
}
