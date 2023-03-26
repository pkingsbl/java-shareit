package ru.practicum.shareitserver.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareitserver.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder(toBuilder = true)
public class ItemRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemDto> items;

}
