package ru.practicum.shareitserver.booking.service;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.Status;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.exception.ValidationException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;
import static ru.practicum.shareitserver.booking.BookingMapper.mapToBooking;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public Booking add(Long userId, BookingDto bookingDto) {
        log.info("User: {}. Add booking {}", userId, bookingDto.toString());
        User user = checkUser(userId);
        Item item = checkItem(bookingDto.getItemId());
        checkForBooking(item, userId);

        return bookingRepository.save(mapToBooking(bookingDto, item, user));
    }

    @Override
    @Transactional
    public Booking approve(Long bookingId, Long userId, Boolean approved) {
        log.info("User: {}. Add approve {} for booking {}", userId, approved, bookingId);
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        checkOwner(booking.getItem(), userId);
        approveStatus(approved, booking);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking findById(Long bookingId, Long userId) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return booking;
        }
        throw new NotFoundException("Бронирование не найдено");
    }

    @Override
    public Collection<Booking> findAllByUser(Long userId, String state, Integer from, Integer size) {
        checkUser(userId);
        LocalDateTime nowTime =  LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        Collection<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByBookerId(userId, pageRequest));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfter(userId, nowTime, nowTime, pageRequest));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByBookerIdAndEndBefore(userId, nowTime, pageRequest));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStartAfter(userId, nowTime, pageRequest));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, pageRequest));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, pageRequest));
                break;
        }
        return bookings;
    }

    @Override
    public Collection<Booking> findAllByOwner(Long userId, String state, Integer from, Integer size) {
        checkUser(userId);
        LocalDateTime nowTime =  LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        Collection<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByItemOwnerId(userId, pageRequest));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, nowTime, nowTime, pageRequest));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, nowTime, pageRequest));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, nowTime, pageRequest));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, pageRequest));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, pageRequest));
                break;
        }
        return bookings;
    }

    private static void approveStatus(Boolean approved, Booking booking) {
        if (booking.getStatus().equals(Status.WAITING)) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        } else {
            throw new ValidationException("Бронирование уже было подтверждено или отклонено ранее");
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    private void checkOwner(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    private void checkForBooking(Item item, Long userId) {
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Вещь ваша, берите так!");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
    }

}
