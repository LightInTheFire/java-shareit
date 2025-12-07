package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
    ItemWithBookingDto getItemOfUserById(long userId, long itemId);

    Collection<ItemWithBookingDto> getAllItemsOfUser(long userId);

    ItemDto saveItem(long userId, NewItemDto newItem);

    ItemDto updateItem(long userId, long itemId, UpdateItemDto updatedItem);

    Collection<ItemDto> searchItems(String query);

    CommentDto createComment(long bookerId, long itemId, NewCommentDto newComment);
}
