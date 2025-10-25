package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

@Slf4j
@Service
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

        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto saveItem(long userId, NewItemDto newItem) {
        User owner = getUserOrThrow(userId);
        Item item = ItemMapper.toItem(newItem);
        item.setOwner(owner);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, UpdateItemDto newItem) {
        getUserOrThrow(userId);
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
        return itemRepository.searchItems(query)
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
