package ru.practicum.shareitserver.user;

import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static Collection<UserDto> mapToUserDto(Collection<User> users) {
        return users.stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }
}
