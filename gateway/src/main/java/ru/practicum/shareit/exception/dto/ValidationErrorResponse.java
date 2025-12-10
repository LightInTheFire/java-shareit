package ru.practicum.shareit.exception.dto;

import java.util.List;

public record ValidationErrorResponse(List<Violation> error) {  //violations are better name but test expect error
}
