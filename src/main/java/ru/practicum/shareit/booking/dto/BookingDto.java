package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {

    private Long id;

    @Future
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;

}
