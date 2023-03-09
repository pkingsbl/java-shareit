package ru.practicum.shareit.request.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Collection;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import javax.transaction.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
    private final ItemRequestService itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository);

    private final User userRequestor = new User(1L, "name", "email@email.com");
    private final User userOwner = new User(2L, "name", "email@email.com");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("my watch doesn't cuckoo, it needs a cuckoo")
            .created(LocalDate.now().atStartOfDay().minusDays(2))
            .requestor(userRequestor)
            .build();
    private final Item itemReturn = Item.builder()
            .id(1L)
            .owner(userOwner)
            .available(true)
            .name("Cuckoo")
            .description("For your watch")
            .request(itemRequest)
            .build();

    @BeforeEach
    void setUp() {
        when(userRepository.findById(userRequestor.getId()))
                .thenReturn(Optional.ofNullable(userRequestor));
        when(userRepository.findById(userOwner.getId()))
                .thenReturn(Optional.ofNullable(userOwner));
        when(itemRequestRepository.save(itemRequest))
                .thenReturn(itemRequest);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userOwner.getId()))
                .thenReturn(List.of(itemRequest));
        when(itemRequestRepository.findAllByRequestorNotLike(userOwner, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "created"))))
                .thenReturn(List.of(itemRequest));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId()))
                .thenReturn(List.of(itemReturn));
    }

    @Test
    void add() {
        itemRequestService.add(userRequestor.getId(), itemRequest);

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userRequestor.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(itemRequest);
    }

    @Test
    void getAllOwner() {
        Collection<ItemRequestDto> itemRequestDtos = itemRequestService.getAllOwner(userOwner.getId());

        assertThat(itemRequestDtos, is(not(empty())));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userOwner.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdOrderByCreatedAsc(userOwner.getId());
    }

    @Test
    void getAllFrom() {
        Collection<ItemRequestDto> itemRequestDtos = itemRequestService.getAllFrom(userOwner.getId(), 1, 10);

        assertThat(itemRequestDtos, is(not(empty())));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userOwner.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorNotLike(userOwner, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "created")));
        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByRequestId(itemRequest.getId());
    }

    @Test
    void getById() {
        itemRequestService.getById(userRequestor.getId(), itemRequest.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userRequestor.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(itemRequest.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByRequestId(itemRequest.getId());
    }

    @Test
    void addUserNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.add(404L, itemRequest));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    void getAllValidationParamFromException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllFrom(userOwner.getId(), -1, 10));

        Assertions.assertEquals("Индекс первого элемента должен быть больше или равен 0", exception.getMessage());
    }

    @Test
    void getAllValidationParamSizeException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllFrom(userOwner.getId(), 1, 0));

        Assertions.assertEquals("Количество элементов для отображения должно быть больше 0", exception.getMessage());
    }

    @Test
    void getByIdNotFoundItemRequestException() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(userRequestor.getId(), 404L));

        Assertions.assertEquals("Запрос не найден", exception.getMessage());
    }

}