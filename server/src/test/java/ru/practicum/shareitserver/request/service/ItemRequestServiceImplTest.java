package ru.practicum.shareitserver.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.exception.ValidationException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceImplTest {

    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final ItemRepository itemRepository;
    @MockBean
    private final ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private final ItemRequestService itemRequestService;

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
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId()))
                .thenReturn(List.of(itemReturn));
    }

    @Test
    void testAddCorrect() {
        itemRequestService.add(userRequestor.getId(), itemRequest);

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userRequestor.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(itemRequest);
    }

    @Test
    void testGetAllOwner() {
        Collection<ItemRequestDto> itemRequestDtos = itemRequestService.getAllOwner(userOwner.getId());

        assertThat(itemRequestDtos, is(not(empty())));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userOwner.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestorIdOrderByCreatedAsc(userOwner.getId());
    }

    @Test
    void testGetAllFrom() {
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
    void testGetById() {
        itemRequestService.getById(userRequestor.getId(), itemRequest.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userRequestor.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(itemRequest.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByRequestId(itemRequest.getId());
    }

    @Test
    void testAddIfUserNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.add(404L, itemRequest));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    void testGetAllValidationParamFromException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllFrom(userOwner.getId(), -1, 10));

        Assertions.assertEquals("Индекс первого элемента должен быть больше или равен 0", exception.getMessage());
    }

    @Test
    void testGetAllValidationParamSizeException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllFrom(userOwner.getId(), 1, 0));

        Assertions.assertEquals("Количество элементов для отображения должно быть больше 0", exception.getMessage());
    }

    @Test
    void testGetByIdNotFoundItemRequestException() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(userRequestor.getId(), 404L));

        Assertions.assertEquals("Запрос не найден", exception.getMessage());
    }

}