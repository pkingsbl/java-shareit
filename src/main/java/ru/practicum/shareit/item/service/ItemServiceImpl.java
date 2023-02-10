package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Objects;

import static ru.practicum.shareit.item.ItemMapper.mapToItem;
import static ru.practicum.shareit.item.ItemMapper.mapToItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        log.info("User: " + userId + ". Add item " + itemDto.toString());
        User user = checkUser(userId);
        Item item = mapToItem(itemDto);
        item.setOwner(user);

        return mapToItemDto(itemRepository.add(item));
    }

    @Override
    public ItemDto change(Long userId, Long itemId, ItemDto itemDto) {
        log.info("User: " + userId + ". Change item " + itemDto.toString());
        checkUser(userId);
        Item item = checkItem(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ForbiddenException("Редактировать вещь может только её владелец!");
        }
        return mapToItemDto(itemRepository.change(itemId, itemDto));
    }

    @Override
    public ItemDto getById(Long itemId) {
        log.info("Get item by id: " + itemId);
        Item item = checkItem(itemId);
        return mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAll(Long userId) {
        log.info("Get all items user: " + userId);
        checkUser(userId);
        return mapToItemDto(itemRepository.get(userId));
    }

    @Override
    public Collection<ItemDto> getSearch(String text) {
        log.info("Search: " + text);
        return mapToItemDto(itemRepository.getSearch(text.toLowerCase()));
    }

    @Override
    public void deleteById(Long userId, Long itemId) {
        log.info("User: " + userId + ". Delete item " + itemId);
        checkUser(userId);
        Item item = checkItem(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ForbiddenException("Удалять вещь может только её владелец!");
        }
        itemRepository.deleteById(itemId);
    }

    private User checkUser(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    private Item checkItem(Long itemId) {
        Item item = itemRepository.getById(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        return item;
    }
}
