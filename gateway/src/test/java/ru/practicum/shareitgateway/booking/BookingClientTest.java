package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitgateway.booking.dto.BookingDto;
import ru.practicum.shareitgateway.booking.dto.State;
import ru.practicum.shareitgateway.exception.ValidationException;
import java.time.LocalDateTime;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingClientTest {

    private final BookingClient bookingClient;

    private final LocalDateTime dateTime = LocalDateTime.now();

    private final BookingDto bookingDto = BookingDto.builder()
            .start(dateTime.plusDays(1))
            .end(dateTime.plusDays(2))
            .itemId(1L)
            .build();

    @Test
    void testAddBookingIfStartNull() {
        final BookingDto booking = BookingDto.builder()
                .end(dateTime.plusDays(2))
                .itemId(1L)
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingClient.add(1L, booking));

        Assertions.assertEquals("Поле даты бронирования не может быть пустым", exception.getMessage());
    }

    @Test
    void testAddBookingIfStartAfterEnd() {
        final BookingDto booking = BookingDto.builder()
                .start(dateTime.plusDays(3))
                .end(dateTime.plusDays(2))
                .itemId(1L)
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingClient.add(1L, booking));

        Assertions.assertEquals("Дата окончания бронирования раньше начала бронирования", exception.getMessage());
    }

    @Test
    void testFindAllByUserIfFromIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingClient.findAllByUser(1L, State.ALL.name(), -1, 10));

        Assertions.assertEquals("Индекс первого элемента должен быть больше или равен 0", exception.getMessage());
    }

    @Test
    void testFindAllByUserIfSizeIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingClient.findAllByUser(1L, State.ALL.toString(), 1, 0));

        Assertions.assertEquals("Количество элементов для отображения должно быть больше 0", exception.getMessage());
    }

    @Test
    void testFindAllByUserIfStateUnknown() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingClient.findAllByUser(1L, "Unknown", 1, 1));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void testFindAllByOwnerIfFromIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingClient.findAllByOwner(1L, State.ALL.name(), -1, 10));

        Assertions.assertEquals("Индекс первого элемента должен быть больше или равен 0", exception.getMessage());
    }

    @Test
    void testFindAllByOwnerIfSizeIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingClient.findAllByOwner(1L, State.ALL.toString(), 1, 0));

        Assertions.assertEquals("Количество элементов для отображения должно быть больше 0", exception.getMessage());
    }

    @Test
    void testFindAllByOwnerIfStateUnknown() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingClient.findAllByOwner(1L, "Unknown", 1, 1));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

}