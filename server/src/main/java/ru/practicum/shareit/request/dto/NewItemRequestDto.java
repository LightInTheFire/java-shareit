package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;

public record NewItemRequestDto(
        @NotBlank String description
) {
}
