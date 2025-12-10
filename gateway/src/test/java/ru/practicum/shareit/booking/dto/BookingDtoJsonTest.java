package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void shouldDeserializeValidBookingDto() throws Exception {
        String content = """
                {
                  "itemId": 1,
                  "start": "%s",
                  "end": "%s"
                }
                """.formatted(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        BookingDto dto = json.parseObject(content);

        assertThat(dto.itemId()).isEqualTo(1);
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailValidationWhenStartInPast() throws Exception {
        String content = """
                {
                  "itemId": 1,
                  "start": "%s",
                  "end": "%s"
                }
                """.formatted(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        BookingDto dto = json.parseObject(content);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("start"));
    }

    @Test
    void shouldFailValidationWhenEndNotInFuture() throws Exception {
        String content = """
                {
                  "itemId": 1,
                  "start": "%s",
                  "end": "%s"
                }
                """.formatted(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now()
        );

        BookingDto dto = json.parseObject(content);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("end"));
    }
}
