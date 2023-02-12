package ru.practicum.shareit.request.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 * id — уникальный идентификатор запроса;
 * description — текст запроса, содержащий описание требуемой вещи; requestor — пользователь, создавший запрос;
 * created — дата и время создания запроса.
 */
@Data
public class ItemRequest {
    private Long id;
    private String description;
    private LocalDate created;
}
