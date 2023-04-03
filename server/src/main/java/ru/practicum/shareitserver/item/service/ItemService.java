package ru.practicum.shareitserver.item.service;

import ru.practicum.shareitserver.item.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto add(Long id, ItemDto itemDto);

    ItemDto change(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getById(Long userId, Long itemId);

    Collection<ItemDto> getAll(Long userId, Integer from, Integer size);

    Collection<ItemDto> getSearch(String text, Integer from, Integer size);

    void deleteById(Long userId, Long itemId);

    CommentDto postComment(Long userId, Long itemId, CommentDto comment);
}
