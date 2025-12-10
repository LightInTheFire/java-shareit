package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Validated
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final RestClient restClient;
    private final String serverUrl;

    public ItemRequestController(@Value("${shareit-server.url}") String baseUrl) {
        this.restClient = RestClient.create();
        this.serverUrl = baseUrl.concat("/requests");
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader(SHARER_USER_ID_HEADER) long requestorId,
            @Valid @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        return restClient.post()
                .uri(serverUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newItemRequestDto)
                .header(SHARER_USER_ID_HEADER, String.valueOf(requestorId))
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsOfUser(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        return restClient.get()
                .uri(serverUrl)
                .header(SHARER_USER_ID_HEADER, String.valueOf(userId))
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        return restClient.get()
                .uri("%s/all".formatted(serverUrl))
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId) {
        return restClient.get()
                .uri("%s/%d".formatted(serverUrl, requestId))
                .retrieve()
                .toEntity(Object.class);
    }
}
