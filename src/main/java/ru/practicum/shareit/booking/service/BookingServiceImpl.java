package ru.practicum.shareit.booking.service;

import java.util.*;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import static ru.practicum.shareit.booking.BookingMapper.mapToBooking;

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
        checkParam(from, size);
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
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    @Override
    public Collection<Booking> findAllByOwner(Long userId, String state, Integer from, Integer size) {
        checkUser(userId);
        checkParam(from, size);
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

    private void checkParam(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Индекс первого элемента должен быть больше или равен 0");
        }
        if (size < 1) {
            throw new ValidationException("Количество элементов для отображения должно быть больше 0");
        }
    }

}
