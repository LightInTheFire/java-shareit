package ru.practicum.shareit.user.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewUserDtoJsonTest {

    @Autowired
    private JacksonTester<NewUserDto> json;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldDeserializeValidUser() throws Exception {
        String content = """
                {
                  "name": "Иван",
                  "email": "ivan@test.com"
                }
                """;

        NewUserDto dto = json.parseObject(content);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailValidationWhenEmailInvalid() throws Exception {
        String content = """
                {
                  "name": "Иван",
                  "email": "not-email"
                }
                """;

        NewUserDto dto = json.parseObject(content);

        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void shouldFailValidationWhenEmailIsNull() throws Exception {
        String content = """
                {
                  "name": "Иван"
                }
                """;

        NewUserDto dto = json.parseObject(content);

        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}
