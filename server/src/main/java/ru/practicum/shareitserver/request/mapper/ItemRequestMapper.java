package ru.practicum.shareitserver.request.mapper;

import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.model.ItemRequest;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static Collection<ItemRequestDto> mapToItemRequestDto(Collection<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::mapToItemRequestDto).collect(Collectors.toList());
    }

}
