package ru.practicum.shareitserver.booking.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import javax.validation.constraints.Future;

import ru.practicum.shareitserver.booking.model.Status;

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
