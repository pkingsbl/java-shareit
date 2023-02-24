package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

/**

 *
 * Получение списка бронирований для всех вещей текущего пользователя.
 * Эндпоинт — GET /bookings/owner?state={state}.
 * Этот запрос имеет смысл для владельца хотя бы одной вещи.
 * Работа параметра state аналогична его работе в предыдущем сценарии.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public Booking add(@RequestHeader(HEADER_ID) Long userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking approve(@RequestHeader(HEADER_ID) Long userId,
                                @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking findById(@RequestHeader(HEADER_ID) Long userId, @PathVariable Long bookingId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public Collection<Booking> findAllByUser(@RequestHeader(HEADER_ID) Long userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<Booking> findAllByOwner(@RequestHeader(HEADER_ID) Long userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByOwner(userId, state);
    }

}

