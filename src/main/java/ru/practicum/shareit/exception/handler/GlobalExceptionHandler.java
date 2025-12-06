package ru.practicum.shareit.exception.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.dto.ErrorResponse;
import ru.practicum.shareit.exception.dto.ValidationErrorResponse;
import ru.practicum.shareit.exception.dto.Violation;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse onException(Exception ex) {
        log.error("Error occurred while processing request {}", ex.getMessage());
        return new ErrorResponse("internal server error",
                "An error occurred while processing request");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException ex
    ) {
        final List<Violation> violations = ex.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        log.warn(violations.toString());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        final List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.warn(violations.toString());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNotFoundException(NotFoundException ex) {
        log.warn("Not found exception occurred while processing request {}", ex.getMessage());
        return new ErrorResponse("not found", ex.getMessage());
    }

    @ExceptionHandler(ItemUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onItemUnavailableException(ItemUnavailableException ex) {
        log.warn("Item unavailable exception occurred while processing request {}", ex.getMessage());
        return new ErrorResponse("unavailable", ex.getMessage());
    }

    @ExceptionHandler(DuplicateDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onDuplicateDataException(DuplicateDataException ex) {
        log.warn("Duplicate data exception occurred while processing request {}", ex.getMessage());
        return new ErrorResponse("duplicate data", ex.getMessage());
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse onForbiddenAccessException(ForbiddenAccessException ex) {
        log.warn("Forbidden access exception occurred while processing request {}", ex.getMessage());
        return new ErrorResponse("forbidden", ex.getMessage());
    }
}
