package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemOfUserById(long userId, long itemId) {
        getUserOrThrow(userId);

        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(NotFoundException.supplier("item with id %d not found", itemId));
    }

    @Override
    public Collection<ItemDto> getAllItemsOfUser(long userId) {
        getUserOrThrow(userId);

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
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
