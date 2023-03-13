package ru.practicum.shareit.item.service;

import java.util.List;
import java.util.Objects;
import java.util.Collection;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
    private final ItemRequestRepository itemRequestRepository;


    @Override
    @Transactional
    public ItemDto add(Long userId, ItemDto itemDto) {
        log.info("User: {}. Add item {}", userId, itemDto.toString());

        User user = checkUser(userId);
        Item item = mapToItem(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = chackItemReq(itemDto.getRequestId());
            item.setRequest(itemRequest);
        }

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
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = chackItemReq(itemDto.getRequestId());
            itemChange.setRequest(itemRequest);
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
    public Collection<ItemDto> getAll(Long userId, Integer from, Integer size) {
        log.info("Get all items user: {}", userId);
        checkUser(userId);
        checkParam(from, size);
        Collection<ItemDto> items =
                mapToItemDto(itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size)));
        items.forEach(itemDto -> {
            findLastAndNextBooking(itemDto);
            Collection<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
            itemDto.setComments(mapToCommentDto(comments));
        });
        return items;
    }

    @Override
    public Collection<ItemDto> getSearch(String text, Integer from, Integer size) {
        log.info("Search: '{}'", text);
        checkParam(from, size);
        Collection<Item> items = itemRepository
                .findAllByNameOrDescriptionContainingIgnoreCase(text, text, PageRequest.of(from / size, size));
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
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private ItemRequest chackItemReq(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
    }

    private static void checkParam(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Индекс первого элемента должен быть больше или равен 0");
        }
        if (size < 1) {
            throw new ValidationException("Количество элементов для отображения должно быть больше 0");
        }
    }

}
