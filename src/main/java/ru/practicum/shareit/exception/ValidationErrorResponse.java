package ru.practicum.shareit.exception;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}
