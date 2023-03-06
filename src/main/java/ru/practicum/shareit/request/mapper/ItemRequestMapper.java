package ru.practicum.shareit.request.mapper;

import java.util.Collection;
import java.util.stream.Collectors;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

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

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requestor(user)
                .build();
    }
}
