package ru.practicum.shareitserver.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.exception.ValidationException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareitserver.item.mapper.ItemMapper.mapToItemDto;
import static ru.practicum.shareitserver.request.mapper.ItemRequestMapper.mapToItemRequestDto;

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
        Collection<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIsNot(user, PageRequest.of(from / size, size, sortByCreated)).stream()
                .collect(Collectors.toList());
        Collection<ItemRequestDto> itemRequestDtos = mapToItemRequestDto(itemRequests);
        itemRequestDtos.forEach(this::findItemsForRequest);

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        log.info("Get requests by id: {}", requestId);
        checkUser(userId);
        ItemRequest itemRequest = checkItemReq(requestId);
        ItemRequestDto itemRequestDto = mapToItemRequestDto(itemRequest);
        findItemsForRequest(itemRequestDto);
        return itemRequestDto;
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
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
        Collection<Item> items = itemRepository.findAllByRequestId(itemRequestDto.getId());
        itemRequestDto.setItems(mapToItemDto(items));
    }

    private ItemRequest checkItemReq(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
    }
}
