package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> findById(long id);

    Collection<Item> findAllByUserId(long userId);

    Item save(Item item);

    Collection<Item> searchItems(String query);
}
