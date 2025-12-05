package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
         //       item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public Item toItem(NewItemDto newItemDto) {
        Item item = new Item();
        item.setName(newItemDto.name());
        item.setDescription(newItemDto.description());
        item.setAvailable(newItemDto.available());
        return item;
    }

    public Item updateItem(Item item, UpdateItemDto updateItemDto) {
        if (updateItemDto.hasName()) {
            item.setName(updateItemDto.name());
        }

        if (updateItemDto.hasDescription()) {
            item.setDescription(updateItemDto.description());
        }

        if (updateItemDto.hasAvailable()) {
            item.setAvailable(updateItemDto.available());
        }

        return item;
    }
}
