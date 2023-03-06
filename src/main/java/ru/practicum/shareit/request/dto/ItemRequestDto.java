package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Collection;
import java.time.LocalDateTime;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
@Builder(toBuilder = true)
public class ItemRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemDto> items;

}
