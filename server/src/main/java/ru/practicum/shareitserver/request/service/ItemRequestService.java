package ru.practicum.shareitserver.request.service;

import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto add(Long userId, ItemRequest itemRequest);

    Collection<ItemRequestDto> getAllOwner(Long userId);

    Collection<ItemRequestDto> getAllFrom(Long userId, Integer from, Integer size);

    ItemRequestDto getById(Long userId, Long requestId);
}
