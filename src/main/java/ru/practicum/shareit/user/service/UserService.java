package ru.practicum.shareit.user.service;

import java.util.Collection;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto getById(Long id);

    UserDto add(User user);

    UserDto change(Long id, User user);

    void deleteById(Long id);
}
