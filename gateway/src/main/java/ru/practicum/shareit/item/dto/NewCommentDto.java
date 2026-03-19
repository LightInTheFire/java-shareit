package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

public record NewCommentDto(@NotBlank String text) {
}
