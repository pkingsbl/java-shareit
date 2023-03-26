package ru.practicum.shareitserver.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.Status;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.ForbiddenException;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.exception.ValidationException;
import ru.practicum.shareitserver.item.dto.CommentDto;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.CommentRepository;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareitserver.booking.BookingMapper.mapToBookingDto;
import static ru.practicum.shareitserver.item.mapper.CommentMapper.mapToComment;
import static ru.practicum.shareitserver.item.mapper.CommentMapper.mapToCommentDto;
import static ru.practicum.shareitserver.item.mapper.ItemMapper.mapToItem;
import static ru.practicum.shareitserver.item.mapper.ItemMapper.mapToItemDto;

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
        Booking last = bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
        Booking next = bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
        itemDto.setLastBooking(mapToBookingDto(last));
        itemDto.setNextBooking(mapToBookingDto(next));
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

}
