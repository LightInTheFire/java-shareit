package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.ItemCommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemWithBookingDto getItemOfUserById(long userId, long itemId) {
        getUserOrThrow(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(NotFoundException.supplier("item with id %d not found", itemId));
        List<Long> itemIdList = List.of(item.getId());
        Map<Long, List<Comment>> commentsOfItems = getCommentsOfItems(itemIdList);
        return ItemMapper.toItemWithBookingDatesDto(
                item,
                null,
                null,
                commentsOfItems.get(itemId));
    }

    @Override
    public Collection<ItemWithBookingDto> getAllItemsOfUser(long userId) {
        getUserOrThrow(userId);
        List<Item> itemsByOwner = itemRepository.findAllByOwnerId(userId);
        List<Long> itemsIds = itemsByOwner.stream()
                .map(Item::getId)
                .toList();

        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookingsOfItems = bookingRepository.findAllByItem_IdInAndStatus(
                itemsIds, BookingStatus.APPROVED);
        Map<Long, Booking> lastBookingsOfItemsById = getLastBookingsMap(bookingsOfItems, currentTime);
        Map<Long, Booking> nextBookingsOfItemsById = getNextBookingsMap(bookingsOfItems, currentTime);
        Map<Long, List<Comment>> commentsByItemId = getCommentsOfItems(itemsIds);

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(item -> ItemMapper.toItemWithBookingDatesDto(
                        item,
                        lastBookingsOfItemsById.get(item.getId()),
                        nextBookingsOfItemsById.get(item.getId()),
                        commentsByItemId.get(item.getId())
                ))
                .toList();
    }

    @Override
    @Transactional
    public ItemDto saveItem(long userId, NewItemDto newItem) {
        User owner = getUserOrThrow(userId);
        Item item = ItemMapper.toItem(newItem);
        item.setOwner(owner);
        if (newItem.requestId() != null) {
            ItemRequest request = requestRepository.findById(newItem.requestId()).orElseThrow(
                    NotFoundException.supplier("Request with id %d not found", newItem.requestId())
            );
            item.setRequest(request);
        }
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, UpdateItemDto newItem) {
        Item item = getItemOrThrow(itemId);
        if (item.getOwner().getId() != userId) {
            throw new ForbiddenAccessException("Only owner of item can update it");
        }
        Item updatedItem = ItemMapper.updateItem(item, newItem);
        itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public Collection<ItemDto> searchItems(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(query)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(long authorId, long itemId, NewCommentDto newComment) {
        User author = getUserOrThrow(authorId);
        Item item = getItemOrThrow(itemId);
        Optional<Booking> bookingOptional = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndTimeIsBefore(
                authorId, itemId, BookingStatus.APPROVED, LocalDateTime.now()
        );

        if (bookingOptional.isEmpty()) {
            throw new ItemCommentException("You can't comment item that you haven't booked");
        }
        Comment comment = CommentMapper.toEntity(newComment, author, item, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toDto(savedComment);
    }

    private Map<Long, Booking> getNextBookingsMap(List<Booking> bookings, LocalDateTime currentTime) {
        Map<Long, Booking> bookingsByItemId = new HashMap<>();

        for (Booking booking : bookings) {
            if (!booking.getStartTime().isAfter(currentTime)) {
                continue;
            }

            Long itemId = booking.getItem().getId();
            Booking existing = bookingsByItemId.get(itemId);

            if (existing == null
                    || booking.getStartTime().isBefore(existing.getStartTime())) {
                bookingsByItemId.put(itemId, booking);
            }
        }

        return bookingsByItemId;
    }

    private Map<Long, List<Comment>> getCommentsOfItems(List<Long> itemsIds) {
        List<Comment> itemsComments = commentRepository.findAllByItem_IdInOrderByCreatedAtDesc(itemsIds);
        return itemsComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
    }

    private Map<Long, Booking> getLastBookingsMap(List<Booking> bookings, LocalDateTime currentTime) {
        Map<Long, Booking> bookingsByItemId = new HashMap<>();

        for (Booking booking : bookings) {
            if (!booking.getEndTime().isAfter(currentTime)) {
                continue;
            }

            Long itemId = booking.getItem().getId();
            Booking existing = bookingsByItemId.get(itemId);

            if (existing == null
                    || booking.getEndTime().isAfter(existing.getEndTime())) {
                bookingsByItemId.put(itemId, booking);
            }
        }

        return bookingsByItemId;
    }

    private Item getItemOrThrow(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                NotFoundException.supplier("Item with id %d not found", itemId)
        );
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(
                NotFoundException.supplier("User with id %d not found", userId)
        );
    }
}
