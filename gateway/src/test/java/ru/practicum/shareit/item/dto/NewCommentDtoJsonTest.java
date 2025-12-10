package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewCommentDtoJsonTest {

    @Autowired
    private JacksonTester<NewCommentDto> json;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldDeserializeValidComment() throws Exception {
        String content = """
                {
                  "text": "Отличная вещь!"
                }
                """;

        NewCommentDto dto = json.parseObject(content);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailValidationWhenTextIsBlank() throws Exception {
        String content = """
                {
                  "text": "   "
                }
                """;

        NewCommentDto dto = json.parseObject(content);

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("text"));
    }
}
