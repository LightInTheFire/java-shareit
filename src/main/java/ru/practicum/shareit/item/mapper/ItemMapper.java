package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
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

    public static ItemWithBookingDto toItemWithBookingDatesDto(
            Item item, Booking nextBooking, Booking lastBooking, List<Comment> comments) {
        Long itemId = item.getId();
        String itemName = item.getName();
        String itemDescription = item.getDescription();
        boolean itemAvailable = item.isAvailable();
        BookingInfoDto lastBookingInfoDto = BookingMapper.toBookingInfoDto(lastBooking).orElse(null);
        BookingInfoDto nextBookingInfoDto = BookingMapper.toBookingInfoDto(nextBooking).orElse(null);
        return new ItemWithBookingDto(
                itemId,
                itemName,
                itemDescription,
                itemAvailable,
                lastBookingInfoDto,
                nextBookingInfoDto,
                comments == null ? null : comments.stream()
                        .map(CommentMapper::toDto)
                        .toList()
        );
    }

}
