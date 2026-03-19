package ru.practicum.shareit.item.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewItemDtoJsonTest {

    @Autowired
    private JacksonTester<NewItemDto> json;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldDeserializeValidItem() throws Exception {
        String content = """
                {
                  "name": "Дрель",
                  "description": "Мощная дрель",
                  "available": true,
                  "requestId": null
                }
                """;

        NewItemDto dto = json.parseObject(content);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() throws Exception {
        String content = """
                {
                  "name": "   ",
                  "description": "Описание",
                  "available": true
                }
                """;

        NewItemDto dto = json.parseObject(content);

        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void shouldFailValidationWhenAvailableIsNull() throws Exception {
        String content = """
                {
                  "name": "Дрель",
                  "description": "Описание",
                  "available": null
                }
                """;

        NewItemDto dto = json.parseObject(content);

        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("available"));
    }
}
