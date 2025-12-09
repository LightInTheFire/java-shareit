package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public record ItemRequestWithResponsesDto(
        Long id,
        String description,
        LocalDateTime created,
        List<ItemForRequestDto> items
) {
}
