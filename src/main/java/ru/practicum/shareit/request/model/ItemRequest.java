package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    Long id;
}
