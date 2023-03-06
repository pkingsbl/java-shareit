package ru.practicum.shareit.item.service;

import java.util.Collection;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;

public interface ItemService {
    ItemDto add(Long id, ItemDto itemDto);

    ItemDto change(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getById(Long userId, Long itemId);

    Collection<ItemDto> getAll(Long userId, Integer from, Integer size);

    Collection<ItemDto> getSearch(String text, Integer from, Integer size);

    void deleteById(Long userId, Long itemId);

    CommentDto postComment(Long userId, Long itemId, CommentDto comment);
}
