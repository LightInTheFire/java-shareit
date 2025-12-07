package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @PathVariable long itemId
    ) {
        return itemService.getItemOfUserById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemWithBookingDto> getItems(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId
    ) {
        return itemService.getAllItemsOfUser(userId);
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @RequestBody @Valid NewItemDto newItemDto
    ) {
        return itemService.saveItem(userId, newItemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @RequestBody @Valid UpdateItemDto updatedItem
    ) {
        return itemService.updateItem(userId, itemId, updatedItem);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @RequestParam(name = "text") String query
    ) {
        return itemService.searchItems(query);
    }
}
