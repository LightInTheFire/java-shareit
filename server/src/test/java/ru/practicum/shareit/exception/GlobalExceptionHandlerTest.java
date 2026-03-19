package ru.practicum.shareit.exception;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(GlobalExceptionHandlerWebMvcTest.TestController.class)
class GlobalExceptionHandlerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Handle NotFoundException")
    void handleNotFoundException() throws Exception {
        mockMvc.perform(get("/test/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("not found"))
                .andExpect(jsonPath("$.message").value("entity not found"));
    }

    @Test
    @DisplayName("Handle ItemUnavailableException")
    void handleItemUnavailableException() throws Exception {
        mockMvc.perform(get("/test/unavailable"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("unavailable"))
                .andExpect(jsonPath("$.message").value("item unavailable"));
    }

    @Test
    @DisplayName("Handle DuplicateDataException")
    void handleDuplicateDataException() throws Exception {
        mockMvc.perform(get("/test/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.name").value("duplicate data"))
                .andExpect(jsonPath("$.message").value("duplicate data"));
    }

    @Test
    @DisplayName("Handle ForbiddenAccessException")
    void handleForbiddenAccessException() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.name").value("forbidden"))
                .andExpect(jsonPath("$.message").value("forbidden access"));
    }

    @Test
    @DisplayName("Handle ItemCommentException")
    void handleItemCommentException() throws Exception {
        mockMvc.perform(get("/test/comment"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("cant comment"))
                .andExpect(jsonPath("$.message").value("cannot comment"));
    }

    @Test
    @DisplayName("Handle BookingIntersectionException")
    void handleBookingIntersectionException() throws Exception {
        mockMvc.perform(get("/test/booking"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("booking intersection"))
                .andExpect(jsonPath("$.message").value("booking conflict"));
    }

    @Test
    @DisplayName("Handle generic Exception")
    void handleGenericException() throws Exception {
        mockMvc.perform(get("/test/runtime"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.name").value("internal server error"))
                .andExpect(jsonPath("$.message").value("An error occurred while processing request"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/notfound")
        public void notFound() {
            throw new NotFoundException("entity not found");
        }

        @GetMapping("/unavailable")
        public void unavailable() {
            throw new ItemUnavailableException("item unavailable");
        }

        @GetMapping("/duplicate")
        public void duplicate() {
            throw new DuplicateDataException("duplicate data");
        }

        @GetMapping("/forbidden")
        public void forbidden() {
            throw new ForbiddenAccessException("forbidden access");
        }

        @GetMapping("/comment")
        public void comment() {
            throw new ItemCommentException("cannot comment");
        }

        @GetMapping("/booking")
        public void booking() {
            throw new BookingIntersectionException("booking conflict");
        }

        @GetMapping("/runtime")
        public void runtime() {
            throw new RuntimeException("generic error");
        }
    }
}
