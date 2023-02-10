package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(Long id);
    UserDto add(User user);

    UserDto change(Long id, User user) ;

    void deleteById(Long id);
}
