package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Builder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import ru.practicum.shareit.booking.dto.BookingDto;

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
}
