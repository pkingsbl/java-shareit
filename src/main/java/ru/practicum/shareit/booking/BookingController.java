package ru.practicum.shareit.booking;

import java.util.Collection;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

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

