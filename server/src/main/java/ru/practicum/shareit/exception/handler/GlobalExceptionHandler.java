package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.dto.ErrorResponse;

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

    @ExceptionHandler(ItemCommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onItemCommentException(ItemCommentException ex) {
        log.warn("Item comment exception occurred while processing request {}", ex.getMessage());
        return new ErrorResponse("cant comment", ex.getMessage());
    }

    @ExceptionHandler(BookingIntersectionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onBookingIntersectionException(BookingIntersectionException ex) {
        log.warn("Booking intersection exception occurred while processing request {}", ex.getMessage());
        return new ErrorResponse("booking intersection", ex.getMessage());
    }
}
