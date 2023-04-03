package ru.practicum.shareitserver.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.data.domain.Sort;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.State;
import ru.practicum.shareitserver.booking.model.Status;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.exception.ValidationException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final ItemRepository itemRepository;
    @MockBean
    private final BookingRepository bookingRepository;
    @InjectMocks
    private final BookingService bookingService;

    private final LocalDateTime dateTime = LocalDateTime.now();
    private final User userReturn = new User(1L, "name", "email@email.com");
    private final User ownerReturn = new User(2L, "name", "email@email.com");
    private final Item itemReturn = Item.builder().id(1L).owner(ownerReturn).available(true).build();
    private final Booking bookingReturn = Booking.builder()
            .id(1L)
            .start(dateTime.plusDays(1))
            .end(dateTime.plusDays(2))
            .item(itemReturn)
            .status(Status.WAITING)
            .booker(userReturn)
            .build();

    @BeforeEach
    public void beforeEach() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userReturn));
        when(itemRepository.findById(itemReturn.getId()))
                .thenReturn(Optional.of(itemReturn));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingReturn);
        when(bookingRepository.findById(bookingReturn.getId()))
                .thenReturn(Optional.of(bookingReturn));
        when(bookingRepository.findAllByBookerId(Mockito.anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(bookingReturn));
        when(bookingRepository.findAllByItemOwnerId(Mockito.anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(bookingReturn));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findAllByBookerIdAndEndBefore(any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findAllByBookerIdAndStartAfter(any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findAllByBookerIdAndStatus(any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findAllByItemOwnerIdAndStatus(any(), any(), any()))
                .thenReturn(List.of());
    }

    @AfterEach
    void tearDown() {
        bookingReturn.setStatus(Status.WAITING);
        itemReturn.setAvailable(true);
    }

    @Test
    void testAddNewBookingCorrect() {
        BookingDto bookingDto = BookingDto.builder()
                .start(dateTime.plusDays(1))
                .end(dateTime.plusDays(2))
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
    void testApproveIfApproveTrue() {
        Booking booking = bookingService.approve(bookingReturn.getId(), ownerReturn.getId(), true);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(bookingReturn.getId());
    }

    @Test
    void testApproveRejectedIfApproveFalse() {
        Booking booking = bookingService.approve(bookingReturn.getId(), ownerReturn.getId(), false);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(bookingReturn.getId());
    }

    @Test
    void testFindById() {
        Booking booking = bookingService.findById(bookingReturn.getId(), userReturn.getId());

        assertThat(booking.getId(), equalTo(bookingReturn.getId()));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findById(bookingReturn.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());

    }

    @Test
    void testFindAllByUserIfStateAll() {
        Collection<Booking> bookings = bookingService.findAllByUser(userReturn.getId(), State.ALL.toString(), 1, 10);

        assertThat(bookings, is(not(empty())));
        assertThat(bookings, hasSize(1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerId(userReturn.getId(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start")));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
    }

    @Test
    void testFindCurrentByUserIfStateCurrent() {
        bookingService.findAllByUser(userReturn.getId(), State.CURRENT.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfter(any(), any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
    }

    @Test
    void testFindPastByUserIfStatePast() {
        bookingService.findAllByUser(userReturn.getId(), State.PAST.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndEndBefore(any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
    }

    @Test
    void testFindFutureByUserIfStateFuture() {
        bookingService.findAllByUser(userReturn.getId(), State.FUTURE.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStartAfter(any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
    }

    @Test
    void testFindWaitingByUserIfStateWaiting() {
        bookingService.findAllByUser(userReturn.getId(), State.WAITING.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
    }

    @Test
    void testFindRejectedByUserIfStateRejected() {
        bookingService.findAllByUser(userReturn.getId(), State.REJECTED.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
    }

    @Test
    void testFindAllByOwnerIfStateAll() {
        Collection<Booking> bookings = bookingService.findAllByOwner(ownerReturn.getId(), State.ALL.toString(), 1, 10);

        assertThat(bookings, is(not(empty())));
        assertThat(bookings, hasSize(1));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerId(ownerReturn.getId(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start")));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
    }

    @Test
    void testFindCurrentByOwnerIfStateCurrent() {
        bookingService.findAllByOwner(ownerReturn.getId(), State.CURRENT.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStartBeforeAndEndAfter(any(), any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
    }

    @Test
    void testFindPastByOwnerIfStatePast() {
        bookingService.findAllByOwner(ownerReturn.getId(), State.PAST.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndBefore(any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
    }

    @Test
    void testFindFutureByOwnerIfStateFuture() {
        bookingService.findAllByOwner(ownerReturn.getId(), State.FUTURE.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStartAfter(any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
    }

    @Test
    void testFindWaitingByOwnerIfStateWaiting() {
        bookingService.findAllByOwner(ownerReturn.getId(), State.WAITING.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
    }

    @Test
    void testFindRejectedByOwnerIfStateRejected() {
        bookingService.findAllByOwner(ownerReturn.getId(), State.REJECTED.toString(), 1, 10);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(any(), any(), any());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(ownerReturn.getId());
    }

    @Test
    void testAddIfBookerOwnerItem() {
        BookingDto bookingDto = BookingDto.builder()
                .start(dateTime.plusDays(1))
                .end(dateTime.plusDays(2))
                .itemId(1L)
                .build();

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.add(ownerReturn.getId(), bookingDto));

        Assertions.assertEquals("Вещь ваша, берите так!", exception.getMessage());
    }

    @Test
    void testAddIfItemAvailableFalse() {
        BookingDto bookingDto = BookingDto.builder()
                .start(dateTime.plusDays(1))
                .end(dateTime.plusDays(2))
                .itemId(1L)
                .build();
        itemReturn.setAvailable(false);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.add(userReturn.getId(), bookingDto));

        Assertions.assertEquals("Вещь недоступна", exception.getMessage());
    }

    @Test
    void testApproveIfItemApproved() {
        bookingReturn.setStatus(Status.APPROVED);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.approve(bookingReturn.getId(), ownerReturn.getId(), false));

        Assertions.assertEquals("Бронирование уже было подтверждено или отклонено ранее", exception.getMessage());
    }

    @Test
    void testApproveIfItemNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approve(bookingReturn.getId(), 404L, true));

        Assertions.assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void testApproveIfBookingNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approve(404L, ownerReturn.getId(), true));

        Assertions.assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void testAddIfItemNotFound() {
        BookingDto bookingDto = BookingDto.builder()
                .start(dateTime.plusDays(1))
                .end(dateTime.plusDays(2))
                .itemId(404L)
                .build();

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.add(userReturn.getId(), bookingDto));

        Assertions.assertEquals("Вещь не найдена", exception.getMessage());
    }
}