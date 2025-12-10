package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    @DisplayName("toDto - should map Comment to CommentDto correctly")
    void toDto_ok() {
        User author = new User(1L, "John", "john@example.com");
        Item item = new Item(2L, "Drill", "Powerful drill", true, author, null);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(3L, "Nice item", item, author, created);

        CommentDto dto = CommentMapper.toDto(comment);

        assertEquals(comment.getId(), dto.id());
        assertEquals(comment.getText(), dto.text());
        assertEquals(comment.getAuthor().getName(), dto.authorName());
        assertEquals(comment.getCreatedAt(), dto.created());
    }

    @Test
    @DisplayName("toEntity - should map NewCommentDto to Comment correctly")
    void toEntity_ok() {
        User author = new User(1L, "John", "john@example.com");
        Item item = new Item(2L, "Drill", "Powerful drill", true, author, null);
        NewCommentDto newCommentDto = new NewCommentDto("Great item!");
        LocalDateTime now = LocalDateTime.now();

        Comment comment = CommentMapper.toEntity(newCommentDto, author, item, now);

        assertNull(comment.getId());
        assertEquals(newCommentDto.text(), comment.getText());
        assertEquals(author, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertEquals(now, comment.getCreatedAt());
    }
}
