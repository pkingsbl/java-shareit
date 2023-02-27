package ru.practicum.shareit.item.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Collection;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.booking.repository.BookingRepository;
import org.springframework.transaction.annotation.Transactional;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItemDto;
import static ru.practicum.shareit.booking.BookingMapper.mapToBookingDto;
import static ru.practicum.shareit.item.mapper.CommentMapper.mapToComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.mapToCommentDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
    public ItemDto getById(Long userId, Long itemId) {
        log.info("Get item by id: {}", itemId);
        Item item = checkItem(itemId);
        ItemDto itemDto = mapToItemDto(item);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            findLastAndNextBooking(itemDto);
        }
        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);
        itemDto.setComments(mapToCommentDto(comments));
        return itemDto;
    }

    @Override
    public Collection<ItemDto> getAll(Long userId) {
        log.info("Get all items user: {}", userId);
        checkUser(userId);
        Collection<ItemDto> items = mapToItemDto(itemRepository.findAllByOwnerId(userId));
        items.forEach(itemDto -> {
            findLastAndNextBooking(itemDto);
            Collection<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
            itemDto.setComments(mapToCommentDto(comments));
        });
        return items;
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

    @Override
    @Transactional
    public CommentDto postComment(Long userId, Long itemId, CommentDto commentDto) {
        checkBooking(userId, itemId);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = mapToComment(commentDto, checkUser(userId), checkItem(itemId));
        return mapToCommentDto(commentRepository.save(comment));
    }

    private void findLastAndNextBooking(ItemDto itemDto) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(itemDto.getId(), Status.APPROVED);
        itemDto.setLastBooking(mapToBookingDto(bookings.size() > 0 ? bookings.get(0) : null));
        itemDto.setNextBooking(mapToBookingDto(bookings.size() > 1 ? bookings.get(1) : null));
    }

    private void checkBooking(Long userId, Long itemId) {
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, Status.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Вы можете оставлять комментарий только на арендованные вещи");
        }
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
