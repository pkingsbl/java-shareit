package ru.practicum.shareitgateway.booking;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.dto.BookingDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;
    private static final String HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HEADER_ID) Long userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(HEADER_ID) Long userId,
                    @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findByIdBooking(@RequestHeader(HEADER_ID) Long userId, @PathVariable Long bookingId) {
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserBooking(@RequestHeader(HEADER_ID) Long userId,
                    @RequestParam(defaultValue = "ALL") String state,
                    @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "5")  Integer size) {
        return bookingClient.findAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerBooking(@RequestHeader(HEADER_ID) Long userId,
                    @RequestParam(defaultValue = "ALL") String state,
                    @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "5")  Integer size) {
        return bookingClient.findAllByOwner(userId, state, from, size);
    }

}

