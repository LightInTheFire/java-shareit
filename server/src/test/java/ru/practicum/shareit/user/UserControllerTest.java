package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Get all users via GET /users")
    void getAllUsersTest() throws Exception {
        UserDto user = new UserDto(1L, "John", "john@example.com");
        Mockito.when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    @DisplayName("Get user by ID via GET /users/{id}")
    void getUserByIdTest() throws Exception {
        UserDto user = new UserDto(1L, "John", "john@example.com");
        Mockito.when(userService.findById(anyLong())).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Create a new user via POST /users")
    void createUserTest() throws Exception {
        NewUserDto newUser = new NewUserDto("John", "john@example.com");
        UserDto savedUser = new UserDto(1L, "John", "john@example.com");

        Mockito.when(userService.save(any(NewUserDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Update user via PATCH /users/{id}")
    void updateUserTest() throws Exception {
        UpdateUserDto updateUser = new UpdateUserDto();
        updateUser.setName("Johnny");
        updateUser.setEmail("johnny@example.com");

        UserDto updatedUser = new UserDto(1L, "Johnny", "johnny@example.com");

        Mockito.when(userService.update(any(UpdateUserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Johnny"))
                .andExpect(jsonPath("$.email").value("johnny@example.com"));
    }

    @Test
    @DisplayName("Delete user via DELETE /users/{id}")
    void deleteUserTest() throws Exception {
        Mockito.doNothing().when(userService).delete(anyLong());

        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isNoContent());
    }
}
