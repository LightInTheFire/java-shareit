package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto getItemOfUserById(long userId, long itemId);

    Collection<ItemWithBookingDto> getAllItemsOfUser(long userId);

    ItemDto saveItem(long userId, NewItemDto newItem);

    ItemDto updateItem(long userId, long itemId, UpdateItemDto updatedItem);

    Collection<ItemDto> searchItems(String query);
}
