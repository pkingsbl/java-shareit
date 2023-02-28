package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import ru.practicum.shareit.booking.model.Status;

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
