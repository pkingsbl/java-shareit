package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.mapToItem;
import static ru.practicum.shareit.item.ItemMapper.mapToItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto add(Long userId, ItemDto itemDto) {
        log.info("User: {}. Add item {}",userId,itemDto.toString());

        User user = checkUser(userId);
        Item item = mapToItem(itemDto);
        item.setOwner(user);

        return mapToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto change(Long userId, Long itemId, ItemDto itemDto) {
        log.info("User: {}. Change item {}", userId, itemDto.toString());
        checkUser(userId);
        Item itemChange = checkItem(itemId);
        if (!Objects.equals(itemChange.getOwner().getId(), userId)) {
            throw new ForbiddenException("Редактировать вещь может только её владелец!");
        }
        if (itemDto.getName() != null) {
            itemChange.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemChange.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemChange.setAvailable(itemDto.getAvailable());
        }
        return mapToItemDto(itemRepository.save(itemChange));
    }

    @Override
    public ItemDto getById(Long itemId) {
        log.info("Get item by id: {}", itemId);
        Item item = checkItem(itemId);
        return mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAll(Long userId) {
        log.info("Get all items user: {}", userId);
        checkUser(userId);
        return mapToItemDto(itemRepository.findAllByOwnerId((userId)));
    }

    @Override
    public Collection<ItemDto> getSearch(String text) {
        log.info("Search: '{}'", text);
        Collection<Item> items = itemRepository.findAllByNameOrDescriptionContainingIgnoreCase(text, text);
        return mapToItemDto(items.stream().filter(Item::getAvailable).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public void deleteById(Long userId, Long itemId) {
        log.info("User: {}. Delete item {}", userId, itemId);
        checkUser(userId);
        Item item = checkItem(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ForbiddenException("Удалять вещь может только её владелец!");
        }
        itemRepository.deleteById(itemId);
    }

    private User checkUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.get();
    }

    private Item checkItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        return item.get();
    }
}
