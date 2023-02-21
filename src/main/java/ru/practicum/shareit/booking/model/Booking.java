package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 * id — уникальный идентификатор бронирования;
 * start — дата и время начала бронирования;
 * end — дата и время конца бронирования;
 * item — вещь, которую пользователь бронирует;
 * booker — пользователь, который осуществляет бронирование;
 * status — статус бронирования. Может принимать одно из следующих значений: WAITING — новое бронирование, ожидает одобрения, APPROVED —
 * бронирование подтверждено владельцем, REJECTED — бронирование отклонено владельцем, CANCELED — бронирование отменено создателем.

 */
@Data
public class Booking {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;
}
