package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.trace("get all users requested");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.trace("get user requested with id: {}", userId);
        return userService.findById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.trace("create user requested with body: {}", newUserDto);
        return userService.save(newUserDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(
            @RequestBody @Valid UpdateUserDto updateUserDto,
            @PathVariable long userId
    ) {
        updateUserDto.setId(userId);
        log.trace("update user requested with id: {} and body {}", userId, updateUserDto);
        return userService.update(updateUserDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.trace("delete user requested with id: {}", userId);
        userService.delete(userId);
    }
}
