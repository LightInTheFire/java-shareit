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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
        Map<Long, Booking> lastBookingsOfItemsById = getLastBookingsMap(itemsIds, currentTime);
        Map<Long, Booking> nextBookingsOfItemsById = getNextBookingMap(itemsIds, currentTime);
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

    private Map<Long, Booking> getNextBookingMap(List<Long> itemsIds, LocalDateTime currentTime) {
        List<Booking> nextBookingsOfItems = bookingRepository.findNextApprovedBookingsForItems(
                itemsIds, currentTime);
        return nextBookingsOfItems.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        Function.identity()
                ));
    }

    private Map<Long, List<Comment>> getCommentsOfItems(List<Long> itemsIds) {
        List<Comment> itemsComments = commentRepository.findAllByItem_IdIn(itemsIds);
        return itemsComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
    }

    private Map<Long, Booking> getLastBookingsMap(List<Long> itemsIds, LocalDateTime currentTime) {
        List<Booking> lastBookingsOfItems = bookingRepository.findLastApprovedBookingsForItems(
                itemsIds, currentTime);
        return lastBookingsOfItems.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        Function.identity()
                ));
    }

    @Override
    @Transactional
    public ItemDto saveItem(long userId, NewItemDto newItem) {
        User owner = getUserOrThrow(userId);
        Item item = ItemMapper.toItem(newItem);
        item.setOwner(owner);
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
