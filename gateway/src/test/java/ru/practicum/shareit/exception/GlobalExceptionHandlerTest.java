package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.dto.ErrorResponse;
import ru.practicum.shareit.exception.dto.ValidationErrorResponse;
import ru.practicum.shareit.exception.dto.Violation;
import ru.practicum.shareit.exception.handler.GlobalExceptionHandler;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerUnitTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Handle IllegalArgumentException")
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("illegal argument");
        ErrorResponse response = handler.onIllegalArgumentException(ex);

        assertEquals("illegal argument", response.name());
        assertEquals("illegal argument", response.message());
    }

    @Test
    @DisplayName("Handle ConstraintViolationException")
    void handleConstraintViolationException() {
        ConstraintViolation<String> violation = new ConstraintViolation<>() {
            @Override
            public String getMessage() {
                return "must not be blank";
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public String getRootBean() {
                return null;
            }

            @Override
            public Class<String> getRootBeanClass() {
                return String.class;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return PathImpl.createPathFromString("field");
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> type) {
                return null;
            }
        };

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
        ValidationErrorResponse response = handler.onConstraintValidationException(ex);

        assertEquals(1, response.error().size());
        Violation v = response.error().getFirst();
        assertEquals("field", v.fieldName());
        assertEquals("must not be blank", v.message());
    }

    @Test
    @DisplayName("Handle generic Exception")
    void handleGenericException() {
        Exception ex = new RuntimeException("runtime error");
        var response = handler.onException(ex);

        assertEquals("internal server error", response.name());
        assertEquals("An error occurred while processing request", response.message());
    }
}
