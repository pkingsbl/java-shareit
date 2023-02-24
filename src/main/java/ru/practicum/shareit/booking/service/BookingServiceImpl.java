package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.booking.BookingMapper.mapToBooking;
import static ru.practicum.shareit.item.ItemMapper.mapToItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService{

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public Booking add(Long userId, BookingDto bookingDto) {
        log.info("User: {}. Add booking {}", userId, bookingDto.toString());
        checkDate(bookingDto);
        User user = checkUser(userId);
        Item item = checkItem(bookingDto.getItemId());
        checkForBooking(item, userId);

        return bookingRepository.save(mapToBooking(bookingDto, item, user));
    }

    @Override
    @Transactional
    public Booking approve(Long bookingId, Long userId, Boolean approved) {
        log.info("User: {}. Add approve {} for booking {}", userId, approved, bookingId);
        Booking booking = checkBooking(bookingId);
        checkOwner(booking.getItem(), userId);
        approveStatus(approved, booking);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking findById(Long bookingId, Long userId) {
        Booking booking = checkBooking(bookingId);
        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return booking;
        }
        throw new NotFoundException("Бронирование не найдено");
    }

    @Override
    public Collection<Booking> findAllByUser(Long userId, String state) {
        User user = checkUser(userId);
        LocalDateTime nowTime =  LocalDateTime.now();
        Sort sortByDate = Sort.by(Sort.Direction.DESC, "start");
        Collection<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByBookerId(userId, sortByDate));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfter(userId, nowTime, nowTime, sortByDate));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByBookerIdAndEndBefore(userId, nowTime, sortByDate));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStartAfter(userId, nowTime, sortByDate));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, sortByDate));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, sortByDate));
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    @Override
    public Collection<Booking> findAllByOwner(Long userId, String state) {
        User user = checkUser(userId);
        LocalDateTime nowTime =  LocalDateTime.now();
        Sort sortByDate = Sort.by(Sort.Direction.DESC, "start");
        Collection<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByItemOwnerId(userId, sortByDate));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository
                        .findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, nowTime, nowTime, sortByDate));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, nowTime, sortByDate));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, nowTime, sortByDate));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, sortByDate));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, sortByDate));
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
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
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.get();
    }

    private Item checkItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        return item.get();
    }

    private Booking checkBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }
        return booking.get();
    }

    private void checkOwner(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    private void checkDate(BookingDto booking) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Дата окончания бронирования раньше начала бронирования");
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