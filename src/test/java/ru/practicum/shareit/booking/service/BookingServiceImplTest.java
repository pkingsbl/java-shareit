package ru.practicum.shareit.booking.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import java.time.LocalDate;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import javax.transaction.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.repository.BookingRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final BookingService bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);

    private final User userReturn = new User(1L, "name", "email@email.com");
    private final User ownerReturn = new User(2L, "name", "email@email.com");
    private final Item itemReturn = Item.builder().id(1L).owner(ownerReturn).available(true).build();
    private final Booking bookingReturn = Booking.builder()
            .id(1L)
            .start(LocalDate.now().atStartOfDay().plusDays(1))
            .end(LocalDate.now().atStartOfDay().plusDays(2))
            .item(itemReturn)
            .status(Status.WAITING)
            .booker(userReturn)
            .build();

    @BeforeEach
    public void beforeEach() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userReturn));
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemReturn));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingReturn);
        when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(bookingReturn));
        when(bookingRepository.findAllByBookerId(Mockito.anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(bookingReturn));
        when(bookingRepository.findAllByItemOwnerId(Mockito.anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(bookingReturn));
    }

    @AfterEach
    void tearDown() {
        bookingReturn.setStatus(Status.WAITING);
        itemReturn.setAvailable(true);
    }

    @Test
    void add() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDate.now().atStartOfDay().plusDays(1))
                .end(LocalDate.now().atStartOfDay().plusDays(2))
                .itemId(1L)
                .build();

        Booking booking = bookingService.add(userReturn.getId(), bookingDto);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(itemReturn.getId());
    }

    @Test
    void approveApprove() {
        Booking booking = bookingService.approve(bookingReturn.getId(), ownerReturn.getId(), true);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(bookingReturn.getId());
    }

    @Test
    void approveRejected() {
        Booking booking = bookingService.approve(bookingReturn.getId(), ownerReturn.getId(), false);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(bookingReturn.getId());
    }

    @Test
    void findById() {
        Booking booking = bookingService.findById(bookingReturn.getId(), userReturn.getId());

        assertThat(booking.getId(), equalTo(bookingReturn.getId()));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(bookingReturn.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());

    }

    @Test
    void findAllByUser() {
        Collection<Booking> bookings = bookingService.findAllByUser(userReturn.getId(), State.ALL.toString(), 1, 10);

        assertThat(bookings, is(not(empty())));
        assertThat(bookings, hasSize(1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerId(userReturn.getId(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start")));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
    }

    @Test
    void findAllByOwner() {
        Collection<Booking> bookings = bookingService.findAllByOwner(ownerReturn.getId(), State.ALL.toString(), 1, 10);

        assertThat(bookings, is(not(empty())));
        assertThat(bookings, hasSize(1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerId(ownerReturn.getId(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start")));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
    }

    @Test
    void addValidationDateException() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDate.now().atStartOfDay().plusDays(2))
                .end(LocalDate.now().atStartOfDay().plusDays(1))
                .itemId(1L)
                .build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.add(userReturn.getId(), bookingDto));

        Assertions.assertEquals("Дата окончания бронирования раньше начала бронирования", exception.getMessage());
    }

    @Test
    void addValidationOwnerException() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDate.now().atStartOfDay().plusDays(1))
                .end(LocalDate.now().atStartOfDay().plusDays(2))
                .itemId(1L)
                .build();

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.add(ownerReturn.getId(), bookingDto));

        Assertions.assertEquals("Вещь ваша, берите так!", exception.getMessage());
    }

    @Test
    void addValidationAvailableException() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDate.now().atStartOfDay().plusDays(1))
                .end(LocalDate.now().atStartOfDay().plusDays(2))
                .itemId(1L)
                .build();
        itemReturn.setAvailable(false);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.add(userReturn.getId(), bookingDto));

        Assertions.assertEquals("Вещь недоступна", exception.getMessage());
    }

    @Test
    void approveValidationStatusException() {
        bookingReturn.setStatus(Status.APPROVED);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.approve(bookingReturn.getId(), ownerReturn.getId(), false));

        Assertions.assertEquals("Бронирование уже было подтверждено или отклонено ранее", exception.getMessage());
    }
}