package ru.practicum.shareit.request.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDate created;
}
