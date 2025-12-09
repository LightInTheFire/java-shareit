package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestWithResponsesDto toItemRequestWithResponseDto(ItemRequest itemRequest, List<Item> items) {
        Long itemRequestId = itemRequest.getId();
        String itemRequestDescription = itemRequest.getDescription();
        LocalDateTime itemRequestCreatedAt = itemRequest.getCreatedAt();
        List<ItemForRequestDto> itemDtos = items == null ? null : items.stream()
                .map(ItemMapper::toItemForRequestDto)
                .toList();
        return new ItemRequestWithResponsesDto(
                itemRequestId,
                itemRequestDescription,
                itemRequestCreatedAt,
                itemDtos
        );
    }


    public ItemRequest toEntity(NewItemRequestDto newItemRequestDto, User requestor, LocalDateTime createdAt) {
        return new ItemRequest(
                null,
                newItemRequestDto.description(),
                requestor,
                createdAt
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        Long itemRequestId = itemRequest.getId();
        String itemRequestDescription = itemRequest.getDescription();
        Long requestorId = itemRequest.getRequestor().getId();
        LocalDateTime itemRequestCreatedAt = itemRequest.getCreatedAt();
        return new ItemRequestDto(
                itemRequestId,
                itemRequestDescription,
                requestorId,
                itemRequestCreatedAt
        );
    }

}
