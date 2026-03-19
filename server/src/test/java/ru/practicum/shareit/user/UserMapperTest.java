package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    @DisplayName("toUserDto - should map User to UserDto correctly")
    void toUserDto_ok() {
        User user = new User(1L, "John Doe", "john@example.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(user.getId(), dto.id());
        assertEquals(user.getName(), dto.name());
        assertEquals(user.getEmail(), dto.email());
    }

    @Test
    @DisplayName("toUser - should map NewUserDto to User correctly")
    void toUser_ok() {
        NewUserDto newUserDto = new NewUserDto("John Doe", "john@example.com");

        User user = UserMapper.toUser(newUserDto);

        assertNull(user.getId());
        assertEquals(newUserDto.name(), user.getName());
        assertEquals(newUserDto.email(), user.getEmail());
    }

    @Test
    @DisplayName("updateUser - should update User fields when UpdateUserDto has values")
    void updateUser_ok() {
        User user = new User(1L, "John", "john@example.com");
        UpdateUserDto update = new UpdateUserDto(null, "Jane", "jane@example.com");

        User updated = UserMapper.updateUser(user, update);

        assertEquals("Jane", updated.getName());
        assertEquals("jane@example.com", updated.getEmail());
    }

    @Test
    @DisplayName("updateUser - should not change User fields when UpdateUserDto has nulls")
    void updateUser_partial() {
        User user = new User(1L, "John", "john@example.com");
        UpdateUserDto update = new UpdateUserDto(1L, null, null);

        User updated = UserMapper.updateUser(user, update);

        assertEquals("John", updated.getName());
        assertEquals("john@example.com", updated.getEmail());
    }
}
