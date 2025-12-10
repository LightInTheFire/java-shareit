package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final RestClient restClient;

    public ItemController(@Value("${shareit-server.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl.concat("/items"))
                .build();
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @PathVariable long itemId
    ) {
        return restClient.get()
                .uri("/" + itemId)
                .header(SHARER_USER_ID_HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId
    ) {
        return restClient.get()
                .header(SHARER_USER_ID_HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @RequestBody @Valid NewItemDto newItemDto
    ) {
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header(SHARER_USER_ID_HEADER, String.valueOf(userId))
                .body(newItemDto)
                .retrieve()
                .toEntity(Object.class);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(SHARER_USER_ID_HEADER) long userId,
            @PathVariable long itemId,
            @RequestBody @Valid UpdateItemDto updatedItem
    ) {
        return restClient.patch()
                .uri("/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(SHARER_USER_ID_HEADER, String.valueOf(userId))
                .body(updatedItem)
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam(name = "text", required = false) String query
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("text", query)
                        .build())
                .retrieve()
                .toEntity(Object.class);

    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(SHARER_USER_ID_HEADER) long authorId,
            @PathVariable long itemId,
            @RequestBody @Valid NewCommentDto comment
    ) {
        return restClient.post()
                .uri("/%d/comment".formatted(itemId))
                .contentType(MediaType.APPLICATION_JSON)
                .header(SHARER_USER_ID_HEADER, String.valueOf(authorId))
                .body(comment)
                .retrieve()
                .toEntity(Object.class);
    }
}
