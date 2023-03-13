package ru.practicum.shareit.request.service;

import java.util.Collection;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {
    ItemRequestDto add(Long userId, ItemRequest itemRequest);

    Collection<ItemRequestDto> getAllOwner(Long userId);

    Collection<ItemRequestDto> getAllFrom(Long userId, Integer from, Integer size);

    ItemRequestDto getById(Long userId, Long requestId);
}
