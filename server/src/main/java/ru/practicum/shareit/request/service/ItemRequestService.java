package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(long requestorId, NewItemRequestDto newItemRequestDto);

    Collection<ItemRequestWithResponsesDto> getAllOfUser(long userId);

    Collection<ItemRequestDto> getAll();

    ItemRequestWithResponsesDto getById(long requestId);
}
