package ru.practicum.shareitserver.item.mapper;

import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.model.Item;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Collection<ItemDto> mapToItemDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

    public static Item mapToItem(ItemDto item) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
