package ru.practicum.shareit.user.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UpdateUserDtoJsonTest {

    @Autowired
    private JacksonTester<UpdateUserDto> json;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldDeserializeWithOnlyName() throws Exception {
        String content = """
                {
                  "name": "Новое имя"
                }
                """;

        UpdateUserDto dto = json.parseObject(content);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailValidationWhenEmailInvalid() throws Exception {
        String content = """
                {
                  "email": "wrong-email"
                }
                """;

        UpdateUserDto dto = json.parseObject(content);

        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}
