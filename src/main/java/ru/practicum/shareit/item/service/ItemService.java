package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto add( Long id, ItemDto itemDto);

    ItemDto change(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getById(Long itemId);

    Collection<ItemDto> getAll(Long userId);

    Collection<ItemDto> getSearch(String text);

    void deleteById(Long userId, Long itemId);

}
