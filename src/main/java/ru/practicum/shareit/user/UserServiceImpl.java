package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto findById(long id) {
        return UserMapper.toUserDto(getUserOrThrow(id));
    }

    @Override
    public UserDto save(NewUserDto newUser) {
        User user = UserMapper.toUser(newUser);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UpdateUserDto newUser) {
        User user = getUserOrThrow(newUser.getId());
        User updatedUser = UserMapper.updateUser(user, newUser);
        userRepository.save(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(long id) {
        getUserOrThrow(id);
        userRepository.deleteById(id);
    }

    private User getUserOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(
                NotFoundException.supplier("User with id %d not found", id)
        );
    }
}
