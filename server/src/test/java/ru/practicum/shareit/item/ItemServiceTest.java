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
import java.util.Collection;
import java.util.List;
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
    @Test
    @DisplayName("getItemOfUserById - returns item with comments")
    void getItemOfUserById_ok() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Comment comment = new Comment(1L, "Nice item", item, owner, LocalDateTime.now());
        when(commentRepository.findAllByItem_IdInOrderByCreatedAtDesc(List.of(item.getId())))
                .thenReturn(List.of(comment));

        ItemWithBookingDto dto = itemService.getItemOfUserById(owner.getId(), item.getId());

        assertNotNull(dto);
        assertEquals(item.getId(), dto.id());
        assertNotNull(dto.comments());
        assertEquals(1, dto.comments().size());
        verify(itemRepository).findById(item.getId());
        verify(commentRepository).findAllByItem_IdInOrderByCreatedAtDesc(List.of(item.getId()));
    }

    @Test
    @DisplayName("getItemOfUserById - throws NotFoundException when item not found")
    void getItemOfUserById_itemNotFound() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.getItemOfUserById(owner.getId(), item.getId()));
    }

    @Test
    @DisplayName("getAllItemsOfUser - returns items with bookings and comments")
    void getAllItemsOfUser_ok() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item));

        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                item, owner, BookingStatus.APPROVED);
        when(bookingRepository.findAllByItem_IdInAndStatus(List.of(item.getId()), BookingStatus.APPROVED))
                .thenReturn(List.of(booking));

        Comment comment = new Comment(1L, "Great item", item, owner, LocalDateTime.now());
        when(commentRepository.findAllByItem_IdInOrderByCreatedAtDesc(List.of(item.getId())))
                .thenReturn(List.of(comment));

        Collection<ItemWithBookingDto> items = itemService.getAllItemsOfUser(owner.getId());

        assertNotNull(items);
        assertEquals(1, items.size());
        ItemWithBookingDto dto = items.iterator().next();
        assertEquals(item.getId(), dto.id());
        assertNotNull(dto.comments());
        assertEquals(1, dto.comments().size());

        verify(itemRepository, times(2)).findAllByOwnerId(owner.getId());
        verify(bookingRepository).findAllByItem_IdInAndStatus(List.of(item.getId()), BookingStatus.APPROVED);
        verify(commentRepository).findAllByItem_IdInOrderByCreatedAtDesc(List.of(item.getId()));
    }

    @Test
    @DisplayName("getAllItemsOfUser - throws NotFoundException when user not found")
    void getAllItemsOfUser_userNotFound() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                itemService.getAllItemsOfUser(owner.getId()));
    }
}
