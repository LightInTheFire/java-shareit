package ru.practicum.shareit.item.dto;

public record NewItemDto(
        String name,
        String description,
        Boolean available,
        Long requestId
) {
}
