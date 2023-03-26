package ru.practicum.shareitserver.user.service;

import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto getById(Long id);

    UserDto add(User user);

    UserDto change(Long id, User user);

    void deleteById(Long id);
}
