package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collection;
import java.util.Objects;
import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto add(User user) {
        log.info("Add user {}", user.toString());
        checkEmail(user, null);
        return mapToUserDto(repository.add(user));
    }

    @Override
    public UserDto change(Long id, User user) {
        log.info("Change user {}: {}", id, user.toString());
        checkEmail(user, id);
        User userChange = repository.change(id, user);
        checkUser(userChange);
        return mapToUserDto(userChange);
    }

    @Override
    public Collection<UserDto> getAll() {
        return mapToUserDto(repository.getAll());
    }

    @Override
    public UserDto getById(Long id) {
        User user = repository.getById(id);
        checkUser(user);
        return mapToUserDto(repository.getById(id));
    }

    @Override
    public void deleteById(Long id) {
        if (repository.deleteById(id) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private static void checkUser(User userChange) {
        if (userChange == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void checkEmail(User user, Long id) {
        if (repository.getAll().stream().anyMatch(userRepository -> userRepository.getEmail().equals(user.getEmail())
                && !Objects.equals(userRepository.getId(), id))) {
            throw new ConflictException("Email пользователей совпадает");
        }
    }

}
