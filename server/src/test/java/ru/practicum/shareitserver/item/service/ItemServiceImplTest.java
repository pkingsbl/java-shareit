package ru.practicum.shareitserver.item.service;

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
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.Status;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.ForbiddenException;
import ru.practicum.shareitserver.exception.NotFoundException;
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

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceImplTest {

    @MockBean
    private final ItemRepository itemRepository;
    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final BookingRepository bookingRepository;
    @MockBean
    private final CommentRepository commentRepository;
    @MockBean
    private final ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private final ItemService itemService;

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
    private final Booking bookingReturn = Booking.builder()
            .id(1L)
            .start(LocalDate.now().atStartOfDay().plusDays(1))
            .end(LocalDate.now().atStartOfDay().plusDays(2))
            .item(itemReturn)
            .status(Status.WAITING)
            .booker(userRequestor)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(itemRequest.getId())
            .name("Cuckoo")
            .description("For your watch")
            .requestId(itemRequest.getId())
            .available(true)
            .build();

    @BeforeEach
    void setUp() {
        when(userRepository.findById(userRequestor.getId()))
                .thenReturn(Optional.ofNullable(userRequestor));
        when(userRepository.findById(userOwner.getId()))
                .thenReturn(Optional.ofNullable(userOwner));
        when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findById(itemReturn.getId()))
                .thenReturn(Optional.ofNullable(itemReturn));
        when(itemRepository.findAllByOwnerId(userOwner.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(itemReturn));
        when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(List.of(itemReturn));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(itemReturn);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookingReturn);
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Mockito.anyLong(), Mockito.anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(List.of(bookingReturn));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(Comment.builder().author(userRequestor).build());
    }

    @Test
    void testAddItemCorrect() {
        ItemDto itemDtoResponse = itemService.add(userOwner.getId(), itemDto);

        assertThat(itemDtoResponse.getId(), notNullValue());
        assertThat(itemDtoResponse.getName(), equalTo(itemReturn.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemReturn.getDescription()));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userOwner.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(itemRequest.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .save(itemReturn);
    }

    @Test
    void testChange() {
        ItemDto itemDtoForChange = ItemDto.builder()
                .id(itemRequest.getId())
                .name("Cu Cuckoo")
                .description("For your tree")
                .available(false)
                .requestId(itemRequest.getId())
                .build();
        ItemDto itemDtoResponse = itemService.change(userOwner.getId(), itemReturn.getId(), itemDtoForChange);

        assertThat(itemDtoResponse.getId(), notNullValue());
        assertThat(itemDtoResponse.getName(), equalTo(itemDtoForChange.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoForChange.getDescription()));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userOwner.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(itemRequest.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(itemReturn.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .save(itemReturn);
    }

    @Test
    void testGetById() {
        ItemDto itemDtoResponse = itemService.getById(userOwner.getId(), itemReturn.getId());

        assertThat(itemDtoResponse.getId(), notNullValue());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(itemReturn.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(any(), any(), any());
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByItemId(itemDtoResponse.getId());
    }

    @Test
    void testGetAll() {
        Collection<ItemDto> itemDtoResponses = itemService.getAll(userOwner.getId(), 1, 10);

        assertThat(itemDtoResponses, is(not(empty())));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userOwner.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByOwnerId(userOwner.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(itemDtoResponses.size()))
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(any(), any(), any());
        Mockito.verify(commentRepository, Mockito.times(itemDtoResponses.size()))
                .findAllByItemId(itemReturn.getId());
    }

    @Test
    void testGetSearch() {
        String text = "Cuckoo";
        Collection<ItemDto> itemDtoResponses = itemService.getSearch(text, 1, 10);

        assertThat(itemDtoResponses, is(not(empty())));
        Mockito.verify(itemRepository, Mockito.times(itemDtoResponses.size()))
                .findAllByNameOrDescriptionContainingIgnoreCase(text, text, PageRequest.of(0, 10));

    }

    @Test
    void testDeleteById() {
        itemService.deleteById(userOwner.getId(), itemReturn.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userOwner.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(itemReturn.getId());
    }

    @Test
    void testPostComment() {
        CommentDto commentDto = CommentDto.builder().id(1L).authorName("Мистер").text("норм").build();
        itemService.postComment(userRequestor.getId(), itemReturn.getId(), commentDto);

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userRequestor.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(itemRequest.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Mockito.anyLong(), Mockito.anyLong(), any(Status.class), any(LocalDateTime.class));
        Mockito.verify(commentRepository, Mockito.times(1))
                .save(any(Comment.class));
    }

    @Test
    void testAddItemIfUserNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.add(404L, itemDto));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testAddItemIfItemRequestNotFound() {
        ItemDto itemDtoItemRequestNotFound = ItemDto.builder()
                .id(itemRequest.getId())
                .name("Cuckoo")
                .description("For your watch")
                .requestId(404L)
                .available(true)
                .build();
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.add(userOwner.getId(), itemDtoItemRequestNotFound));

        Assertions.assertEquals("Запрос не найден", exception.getMessage());
    }

    @Test
    void testChangeItemIfItemNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.change(userOwner.getId(), 404L, itemDto));

        Assertions.assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void testDeleteByIdIfUserNotOwner() {
        final ForbiddenException exception = Assertions.assertThrows(
                ForbiddenException.class,
                () -> itemService.deleteById(userRequestor.getId(), itemReturn.getId()));

        Assertions.assertEquals("Удалять вещь может только её владелец!", exception.getMessage());

    }

}