package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(
            @RequestHeader(SHARER_USER_ID_HEADER) long requestorId,
            @Valid @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        return itemRequestService.create(requestorId, newItemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestWithResponsesDto> getAllRequestsOfUser(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        return itemRequestService.getAllOfUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequests() {
        return itemRequestService.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithResponsesDto getRequestById(@PathVariable long requestId) {
        return itemRequestService.getById(requestId);
    }
}
