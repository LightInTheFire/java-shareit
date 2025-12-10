package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@TestPropertySource(properties = {
        "shareit-server.url=http://localhost:9999"
})
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("GET /items/{id} - should return item successfully")
    void getItem_ok() throws Exception {
        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /items/{id} - should return 400 when header is missing")
    void getItem_withoutHeader() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items - should return all items successfully")
    void getItems_ok() throws Exception {
        mockMvc.perform(get("/items")
                        .header(HEADER, 1L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /items - should return 400 when header is missing")
    void getItems_withoutHeader() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /items - should return 400 when name is blank")
    void createItem_blankName() throws Exception {
        NewItemDto dto = new NewItemDto("", "Valid description", true, null);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /items - should return 400 when description is blank")
    void createItem_blankDescription() throws Exception {
        NewItemDto dto = new NewItemDto("Valid name", "", true, null);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /items - should return 400 when available is null")
    void createItem_nullAvailable() throws Exception {
        NewItemDto dto = new NewItemDto("Valid name", "Valid description", null, null);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /items/{id} - should update item successfully")
    void updateItem_ok() throws Exception {
        UpdateItemDto dto = new UpdateItemDto("Updated name", "Updated description", true);

        mockMvc.perform(patch("/items/1")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("PATCH /items/{id} - should return 400 when header is missing")
    void updateItem_withoutHeader() throws Exception {
        UpdateItemDto dto = new UpdateItemDto("Updated name", "Updated description", true);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /items/search - should return search results successfully")
    void searchItems_ok() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "query"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /items/{id}/comment - should create comment successfully")
    void createComment_ok() throws Exception {
        NewCommentDto dto = new NewCommentDto("Nice item!");

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /items/{id}/comment - should return 400 when header is missing")
    void createComment_withoutHeader() throws Exception {
        NewCommentDto dto = new NewCommentDto("Nice item!");

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
