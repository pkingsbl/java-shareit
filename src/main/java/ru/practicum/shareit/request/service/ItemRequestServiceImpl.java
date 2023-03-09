package ru.practicum.shareit.request.service;

import java.util.Collection;
import java.util.Optional;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItemDto;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.mapToItemRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    @Transactional
    public ItemRequestDto add(Long userId, ItemRequest itemRequest) {
        log.info("User: {}. Add request {}", userId, itemRequest.toString());

        User user = checkUser(userId);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        return mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestDto> getAllOwner(Long userId) {
        log.info("Get all requests user: {}", userId);
        checkUser(userId);
        Collection<ItemRequestDto> itemRequestDtos =
                mapToItemRequestDto(itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId));
        itemRequestDtos.forEach(this::findItemsForRequest);

        return itemRequestDtos;
    }

    @Override
    public Collection<ItemRequestDto> getAllFrom(Long userId, Integer from, Integer size) {
        log.info("Get all requests from: {} size: {}", from, size);
        User user = checkUser(userId);
        checkParam(from, size);
        Sort sortByCreated = Sort.by(Sort.Direction.ASC, "created");
        Collection<ItemRequest> itemRequests =
                itemRequestRepository.findAllByRequestorNotLike(user, PageRequest.of(from / size, size, sortByCreated));
        Collection<ItemRequestDto> itemRequestDtos = mapToItemRequestDto(itemRequests);
        itemRequestDtos.forEach(this::findItemsForRequest);

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        log.info("Get requests by id: {}", requestId);
        checkUser(userId);
        ItemRequest itemRequest = chackItemReq(requestId);
        ItemRequestDto itemRequestDto = mapToItemRequestDto(itemRequest);
        findItemsForRequest(itemRequestDto);
        return itemRequestDto;
    }

    private User checkUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.get();
    }

    private static void checkParam(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Индекс первого элемента должен быть больше или равен 0");
        }
        if (size < 1) {
            throw new ValidationException("Количество элементов для отображения должно быть больше 0");
        }
    }

    private void findItemsForRequest(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(mapToItemDto(itemRepository.findAllByRequestId(itemRequestDto.getId())));
    }

    private ItemRequest chackItemReq(Long requestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            throw new NotFoundException("Запрос не найден");
        }
        return itemRequest.get();
    }
}
