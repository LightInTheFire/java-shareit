package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    @DisplayName("toItemDto - should map Item to ItemDto correctly")
    void toItemDto_ok() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(2L, "Drill", "Powerful drill", true, owner, null);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), dto.id());
        assertEquals(item.getName(), dto.name());
        assertEquals(item.getDescription(), dto.description());
        assertEquals(item.isAvailable(), dto.available());
    }

    @Test
    @DisplayName("toItem - should create Item from NewItemDto")
    void toItem_ok() {
        NewItemDto newItemDto = new NewItemDto("Drill", "Powerful drill", true, null);

        Item item = ItemMapper.toItem(newItemDto);

        assertEquals(newItemDto.name(), item.getName());
        assertEquals(newItemDto.description(), item.getDescription());
        assertEquals(newItemDto.available(), item.isAvailable());
    }

    @Test
    @DisplayName("updateItem - should update fields of Item if present")
    void updateItem_ok() {
        Item item = new Item();
        item.setName("Old Name");
        item.setDescription("Old Desc");
        item.setAvailable(false);

        UpdateItemDto update = new UpdateItemDto("New Name", "New Desc", true);

        Item updated = ItemMapper.updateItem(item, update);

        assertEquals("New Name", updated.getName());
        assertEquals("New Desc", updated.getDescription());
        assertTrue(updated.isAvailable());
    }

    @Test
    @DisplayName("updateItem - should not update fields if null in UpdateItemDto")
    void updateItem_partial() {
        Item item = new Item();
        item.setName("Old Name");
        item.setDescription("Old Desc");
        item.setAvailable(false);

        UpdateItemDto update = new UpdateItemDto(null, null, null);

        Item updated = ItemMapper.updateItem(item, update);

        assertEquals("Old Name", updated.getName());
        assertEquals("Old Desc", updated.getDescription());
        assertFalse(updated.isAvailable());
    }

    @Test
    @DisplayName("toItemWithBookingDatesDto - should map Item with bookings and comments correctly")
    void toItemWithBookingDatesDto_ok() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(2L, "Drill", "Powerful drill", true, owner, null);

        User booker = new User(3L, "John", "john@example.com");
        Booking lastBooking = new Booking(10L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, booker, null);
        Booking nextBooking = new Booking(11L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, null);

        Comment comment = new Comment(1L, "Nice item", item, booker, LocalDateTime.now());

        ItemWithBookingDto dto = ItemMapper.toItemWithBookingDatesDto(item, nextBooking, lastBooking, List.of(comment));

        assertEquals(item.getId(), dto.id());
        assertEquals(item.getName(), dto.name());
        assertEquals(item.getDescription(), dto.description());
        assertEquals(item.isAvailable(), dto.available());
        assertNotNull(dto.lastBooking());
        assertNotNull(dto.nextBooking());
        assertNotNull(dto.comments());
        assertEquals(1, dto.comments().size());
    }

    @Test
    @DisplayName("toItemForRequestDto - should map Item to ItemForRequestDto correctly")
    void toItemForRequestDto_ok() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(2L, "Drill", "Powerful drill", true, owner, null);

        ItemForRequestDto dto = ItemMapper.toItemForRequestDto(item);

        assertEquals(item.getId(), dto.id());
        assertEquals(item.getName(), dto.name());
        assertEquals(owner.getId(), dto.ownerId());
    }
}
