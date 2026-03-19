package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    @DisplayName("toItemRequestWithResponseDto - should map ItemRequest and items to ItemRequestWithResponsesDto")
    void toItemRequestWithResponseDto_ok() {
        User requestor = new User(1L, "John", "john@example.com");
        ItemRequest request = new ItemRequest(2L, "Need drill", requestor, LocalDateTime.now());

        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(3L, "Drill", "Powerful drill", true, owner, null);

        ItemRequestWithResponsesDto dto = ItemRequestMapper.toItemRequestWithResponseDto(request, List.of(item));

        assertEquals(request.getId(), dto.id());
        assertEquals(request.getDescription(), dto.description());
        assertEquals(request.getCreatedAt(), dto.created());
        assertNotNull(dto.items());
        assertEquals(1, dto.items().size());
        ItemForRequestDto itemDto = dto.items().getFirst();
        assertEquals(item.getId(), itemDto.id());
        assertEquals(item.getName(), itemDto.name());
        assertEquals(owner.getId(), itemDto.ownerId());
    }

    @Test
    @DisplayName("toEntity - should map NewItemRequestDto to ItemRequest correctly")
    void toEntity_ok() {
        User requestor = new User(1L, "John", "john@example.com");
        NewItemRequestDto newRequestDto = new NewItemRequestDto("Need drill");
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = ItemRequestMapper.toEntity(newRequestDto, requestor, now);

        assertNull(request.getId());
        assertEquals(newRequestDto.description(), request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertEquals(now, request.getCreatedAt());
    }

    @Test
    @DisplayName("toItemRequestDto - should map ItemRequest to ItemRequestDto correctly")
    void toItemRequestDto_ok() {
        User requestor = new User(1L, "John", "john@example.com");
        ItemRequest request = new ItemRequest(2L, "Need drill", requestor, LocalDateTime.now());

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);

        assertEquals(request.getId(), dto.id());
        assertEquals(request.getDescription(), dto.description());
        assertEquals(requestor.getId(), dto.requestorId());
        assertEquals(request.getCreatedAt(), dto.created());
    }
}
