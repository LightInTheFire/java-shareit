package ru.practicum.shareit.exception.dto;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}
