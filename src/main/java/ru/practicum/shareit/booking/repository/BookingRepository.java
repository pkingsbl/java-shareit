package ru.practicum.shareit.booking.repository;

import java.util.Collection;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerId(Long userId, PageRequest of);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime start, LocalDateTime end, PageRequest of);

    Collection<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime end, PageRequest of);

    Collection<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime start, PageRequest of);

    Collection<Booking> findAllByBookerIdAndStatus(Long userId, Status status, PageRequest of);

    Collection<Booking> findAllByItemOwnerId(Long userId, PageRequest of);

    Collection<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime start, LocalDateTime end, PageRequest of);

    Collection<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime end, PageRequest of);

    Collection<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime start, PageRequest of);

    Collection<Booking> findAllByItemOwnerIdAndStatus(Long userId, Status status, PageRequest of);

    Booking findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long id, Status status, LocalDateTime start);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long id, Status status, LocalDateTime start);

    Collection<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status approved, LocalDateTime now);

}