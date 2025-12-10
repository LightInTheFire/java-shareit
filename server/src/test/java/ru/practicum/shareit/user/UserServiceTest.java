package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
    }

    @Test
    @DisplayName("findAll - should return list of users")
    void findAll_ok() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.findAll().stream().toList();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).name());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("findById - should return user when exists")
    void findById_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        assertEquals("John Doe", result.name());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("findById - should throw NotFoundException when user does not exist")
    void findById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("save - should save and return user")
    void save_ok() {
        NewUserDto newUserDto = new NewUserDto("John Doe", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.save(newUserDto);

        assertEquals("John Doe", result.name());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("update - should update and return user")
    void update_ok() {
        UpdateUserDto updateUserDto = new UpdateUserDto(1L, "Jane Doe", "jane@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(updateUserDto);

        assertEquals("Jane Doe", result.name());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("update - should throw NotFoundException when user does not exist")
    void update_notFound() {
        UpdateUserDto updateUserDto = new UpdateUserDto(1L, "Jane Doe", "jane@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(updateUserDto));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("delete - should delete user when exists")
    void delete_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete - should throw NotFoundException when user does not exist")
    void delete_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
