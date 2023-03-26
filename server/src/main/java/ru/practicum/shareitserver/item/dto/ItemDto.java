package ru.practicum.shareitserver.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareitserver.booking.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Builder(toBuilder = true)
public class ItemDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Long requestId;
    private Collection<CommentDto> comments;

}
