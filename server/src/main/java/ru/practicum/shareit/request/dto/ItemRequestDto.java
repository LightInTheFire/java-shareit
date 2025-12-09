package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

public record ItemRequestDto(Long id, String description, long requestorId, LocalDateTime created) {
}
