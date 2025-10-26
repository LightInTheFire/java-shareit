package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> findAllByUserId(long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    @Override
    public Item save(Item item) {
        Long id = generateNextId();
        item.setId(id);

        items.put(id, item);

        return item;
    }

    @Override
    public Collection<Item> searchItems(String query) {
        String lowercaseQuery = query.trim().toLowerCase();

        return items.values()
                .stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowercaseQuery)
                        || item.getDescription().toLowerCase().contains(lowercaseQuery))
                .toList();
    }

    private Long generateNextId() {
        Long nextId = items.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return ++nextId;
    }
}
