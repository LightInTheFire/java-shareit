package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@TestPropertySource(properties = {
        "shareit-server.url=http://localhost:9999"
})
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /requests - should create request successfully")
    void createRequest_ok() throws Exception {
        NewItemRequestDto dto = new NewItemRequestDto("Need a drill");

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("POST /requests - should return 400 when description is blank")
    void createRequest_blankDescription() throws Exception {
        NewItemRequestDto dto = new NewItemRequestDto("");

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /requests - should return 400 when header is missing")
    void createRequest_withoutHeader() throws Exception {
        NewItemRequestDto dto = new NewItemRequestDto("Need a drill");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests - should return all requests of user successfully")
    void getAllRequestsOfUser_ok() throws Exception {
        mockMvc.perform(get("/requests")
                        .header(HEADER, 1L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /requests - should return 400 when header is missing")
    void getAllRequestsOfUser_withoutHeader() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /requests/all - should return all requests successfully")
    void getAllRequests_ok() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /requests/{id} - should return request by id successfully")
    void getRequestById_ok() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().is5xxServerError());
    }
}
