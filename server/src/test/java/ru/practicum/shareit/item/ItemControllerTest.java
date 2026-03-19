package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    @DisplayName("Get item by ID via GET /items/{itemId}")
    void getItemTest() throws Exception {
        ItemWithBookingDto responseDto = new ItemWithBookingDto(
                1L,
                "ItemName",
                "ItemDescription",
                true,
                new BookingInfoDto(10L, 2L, LocalDateTime.now().minusDays(1), LocalDateTime.now()),
                new BookingInfoDto(11L, 3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                List.of(new CommentDto(1L, "Nice item!", "Alice", LocalDateTime.now()))
        );

        Mockito.when(itemService.getItemOfUserById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ItemName"))
                .andExpect(jsonPath("$.lastBooking.id").value(10L))
                .andExpect(jsonPath("$.nextBooking.id").value(11L))
                .andExpect(jsonPath("$.comments[0].text").value("Nice item!"));
    }

    @Test
    @DisplayName("Get all items of user via GET /items")
    void getItemsTest() throws Exception {
        ItemWithBookingDto itemDto = new ItemWithBookingDto(
                1L,
                "ItemName",
                "ItemDescription",
                true,
                null,
                null,
                List.of()
        );

        Mockito.when(itemService.getAllItemsOfUser(anyLong()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("ItemName"));
    }

    @Test
    @DisplayName("Create a new item via POST /items")
    void createItemTest() throws Exception {
        NewItemDto newItemDto = new NewItemDto(
                "ItemName",
                "ItemDescription",
                true,
                null
        );

        ItemDto responseDto = new ItemDto(
                1L,
                "ItemName",
                "ItemDescription",
                true
        );

        Mockito.when(itemService.saveItem(anyLong(), any(NewItemDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ItemName"));
    }

    @Test
    @DisplayName("Update item via PATCH /items/{itemId}")
    void updateItemTest() throws Exception {
        UpdateItemDto updatedItem = new UpdateItemDto(
                "UpdatedName",
                "UpdatedDescription",
                true
        );

        ItemDto responseDto = new ItemDto(
                1L,
                "UpdatedName",
                "UpdatedDescription",
                true
        );

        Mockito.when(itemService.updateItem(anyLong(), anyLong(), any(UpdateItemDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header(HEADER_USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.description").value("UpdatedDescription"));
    }

    @Test
    @DisplayName("Search items via GET /items/search")
    void searchItemsTest() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "ItemName",
                "ItemDescription",
                true
        );

        Mockito.when(itemService.searchItems(anyString()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "ItemName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("ItemName"));
    }

    @Test
    @DisplayName("Create a comment via POST /items/{itemId}/comment")
    void createCommentTest() throws Exception {
        NewCommentDto newComment = new NewCommentDto("Great item!");

        CommentDto responseDto = new CommentDto(
                1L,
                "Great item!",
                "John",
                LocalDateTime.now()
        );

        Mockito.when(itemService.createComment(anyLong(), anyLong(), any(NewCommentDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(HEADER_USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"));
    }
}
