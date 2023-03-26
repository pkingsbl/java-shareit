package ru.practicum.shareitserver.booking.service;

import java.util.Collection;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.dto.BookingDto;

public interface BookingService {

    Booking add(Long userId, BookingDto bookingDto);

    Booking approve(Long bookingId, Long userId, Boolean approved);

    Booking findById(Long bookingId, Long userId);

    Collection<Booking> findAllByUser(Long userId, String state, Integer from, Integer size);

    Collection<Booking> findAllByOwner(Long userId, String state, Integer from, Integer size);
}
