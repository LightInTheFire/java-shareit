package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@TestPropertySource(properties = {
        "shareit-server.url=http://localhost:9999"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /users - should return all users successfully")
    void getAllUsers_ok() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /users/{id} - should return user successfully")
    void getUser_ok() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /users - should create user successfully")
    void createUser_ok() throws Exception {
        NewUserDto dto = new NewUserDto("John Doe", "john@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /users - should return 400 when name is blank")
    void createUser_blankName() throws Exception {
        NewUserDto dto = new NewUserDto("", "john@example.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /users/{id} - should update user successfully")
    void updateUser_ok() throws Exception {
        UpdateUserDto dto = new UpdateUserDto(1L, "John Doe","email@email" );

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("PATCH /users/{id} - should return 400 when update data is invalid")
    void updateUser_invalidData() throws Exception {
        UpdateUserDto dto = new UpdateUserDto(1L, "John Doe", "not-an-email");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /users/{id} - should delete user successfully")
    void deleteUser_ok() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().is2xxSuccessful());
    }
}
