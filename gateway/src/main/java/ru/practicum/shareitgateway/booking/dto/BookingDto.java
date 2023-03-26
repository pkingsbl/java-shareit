package ru.practicum.shareitgateway.booking.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class BookingDto {

    private Long id;
    @NotNull
    private Long itemId;
    @Future
    private LocalDateTime start;
    @Future
    private LocalDateTime end;

}
