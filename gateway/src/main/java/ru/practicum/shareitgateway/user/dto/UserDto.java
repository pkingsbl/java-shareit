package ru.practicum.shareitgateway.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserDto {

    private String name;
    private String email;

}
