package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public User toUser(NewUserDto newUser) {
        User user = new User();
        user.setName(newUser.name());
        user.setEmail(newUser.email());
        return user;
    }

    public User updateUser(User user, UpdateUserDto updatedUser) {
        if (updatedUser.hasName()) {
            user.setName(updatedUser.getName());
        }

        if (updatedUser.hasEmail()) {
            user.setEmail(updatedUser.getEmail());
        }

        return user;
    }
}
