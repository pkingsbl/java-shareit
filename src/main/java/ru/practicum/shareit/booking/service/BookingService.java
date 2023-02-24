package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {

    Booking add(Long userId, BookingDto bookingDto);

    Booking approve(Long bookingId, Long userId, Boolean approved);

    Booking findById(Long bookingId, Long userId);

    Collection<Booking> findAllByUser(Long userId, String state);

    Collection<Booking> findAllByOwner(Long userId, String state);
}
