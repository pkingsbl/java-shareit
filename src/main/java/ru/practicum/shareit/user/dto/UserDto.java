package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
