package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    @DisplayName("Create a new item request via POST /requests")
    void createRequestTest() throws Exception {
        NewItemRequestDto newRequestDto = new NewItemRequestDto("Need a drill");

        ItemRequestDto responseDto = new ItemRequestDto(
                1L,
                "Need a drill",
                2L,
                LocalDateTime.now()
        );

        Mockito.when(itemRequestService.create(anyLong(), any(NewItemRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.requestorId").value(2L));
    }

    @Test
    @DisplayName("Get all item requests of user via GET /requests")
    void getAllRequestsOfUserTest() throws Exception {
        ItemRequestWithResponsesDto responseDto = new ItemRequestWithResponsesDto(
                1L,
                "Need a drill",
                LocalDateTime.now(),
                List.of(new ItemForRequestDto(10L, "Drill", 5L))
        );

        Mockito.when(itemRequestService.getAllOfUser(anyLong()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need a drill"))
                .andExpect(jsonPath("$[0].items[0].id").value(10L))
                .andExpect(jsonPath("$[0].items[0].name").value("Drill"))
                .andExpect(jsonPath("$[0].items[0].ownerId").value(5L));
    }

    @Test
    @DisplayName("Get all item requests via GET /requests/all")
    void getAllRequestsTest() throws Exception {
        ItemRequestDto responseDto = new ItemRequestDto(
                1L,
                "Need a drill",
                2L,
                LocalDateTime.now()
        );

        Mockito.when(itemRequestService.getAll())
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need a drill"))
                .andExpect(jsonPath("$[0].requestorId").value(2L));
    }

    @Test
    @DisplayName("Get item request by ID via GET /requests/{requestId}")
    void getRequestByIdTest() throws Exception {
        ItemRequestWithResponsesDto responseDto = new ItemRequestWithResponsesDto(
                1L,
                "Need a drill",
                LocalDateTime.now(),
                List.of(new ItemForRequestDto(10L, "Drill", 5L))
        );

        Mockito.when(itemRequestService.getById(anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/requests/{requestId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.items[0].id").value(10L))
                .andExpect(jsonPath("$.items[0].name").value("Drill"))
                .andExpect(jsonPath("$.items[0].ownerId").value(5L));
    }
}
