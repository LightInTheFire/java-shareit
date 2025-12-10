package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final RestClient restClient;
    private final String serverUrl;

    public UserController(@Value("${shareit-server.url}") String baseUrl) {
        this.restClient = RestClient.create();

        this.serverUrl = baseUrl.concat("/users");
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.trace("get all users requested");
        return restClient.get()
                .uri(serverUrl)
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.trace("get user requested with id: {}", userId);
        return restClient.get()
                .uri("%s/%d".formatted(serverUrl, userId))
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.trace("create user requested with body: {}", newUserDto);
        return restClient.post()
                .uri(serverUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newUserDto)
                .retrieve()
                .toEntity(Object.class);

    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @RequestBody @Valid UpdateUserDto updateUserDto,
            @PathVariable long userId
    ) {
        log.trace("update user requested with id: {} and body {}", userId, updateUserDto);
        return restClient.patch()
                .uri("%s/%d".formatted(serverUrl, userId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateUserDto)
                .retrieve()
                .toEntity(Object.class);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.trace("delete user requested with id: {}", userId);
        restClient.delete()
                .uri("%s/%d".formatted(serverUrl, userId));
    }
}
