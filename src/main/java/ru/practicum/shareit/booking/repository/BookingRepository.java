package ru.practicum.shareit.booking.repository;

import java.util.List;
import java.util.Collection;
import java.time.LocalDateTime;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerId(Long userId, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    Collection<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime end, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime start, Sort sort);

    Collection<Booking> findAllByBookerIdAndStatus(Long userId, Status status, Sort sort);

    Collection<Booking> findAllByItemOwnerId(Long userId, Sort sort);

    Collection<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    Collection<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime end, Sort sort);

    Collection<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime start, Sort sort);

    Collection<Booking> findAllByItemOwnerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findAllByItemIdAndStatusOrderByStartAsc(Long id, Status status);

    Collection<Object> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status approved, LocalDateTime now);

}
