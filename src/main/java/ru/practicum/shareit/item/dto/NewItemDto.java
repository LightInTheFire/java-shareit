package ru.practicum.shareit.item.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewItemDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull Boolean available,
        @Nullable Long requestId
) {
}
