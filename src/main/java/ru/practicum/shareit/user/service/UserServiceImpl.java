package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto add(User user) {
        log.info("Add user " + user.toString());
        if (repository.getAll().stream().anyMatch(userRepository -> userRepository.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Email пользователей совпадает");
        }
        return UserMapper.mapToItemDto(repository.add(user));
    }

    @Override
    public UserDto change(Long id, User user) {
        log.info("Change user " + id + ": " + user.toString());
        if (repository.getAll().stream().anyMatch(userRepository -> userRepository.getEmail().equals(user.getEmail())
                && !Objects.equals(userRepository.getId(), id))) {
            throw new ConflictException("Email пользователей совпадает");
        }
        User userChange = repository.change(id, user);
        if (userChange == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return UserMapper.mapToItemDto(userChange);
    }

    @Override
    public List<UserDto> getAll() {
        Collection<User> users =  repository.getAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(UserMapper.mapToItemDto(user));
        }
        return userDtos;
    }

    @Override
    public UserDto getById(Long id) {
        User user = repository.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return UserMapper.mapToItemDto(repository.getById(id));
    }

    @Override
    public void deleteById(Long id) {
        if (repository.deleteById(id) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

}
