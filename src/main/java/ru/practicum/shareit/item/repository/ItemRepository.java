package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item add(Item item);

    Item change(Long itemId, ItemDto itemDto);

    Item getById(Long itemId);

    Collection<Item> get(Long userId);

    Collection<Item> getSearch(String text);

    Item deleteById(Long itemId);

}
