package ru.practicum.shareit.user.service;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import static ru.practicum.shareit.user.UserMapper.mapToUserDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAll() {
        log.info("Get all user by id");

        return mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long id) {
        log.info("Get user by id {}", id);
        User user = checkUser(id);

        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto add(User user) {
        log.info("Add user {}", user.toString());

        return mapToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto change(Long id, User user) {
        log.info("Change user {}: {}", id, user.toString());
        User userChange = checkUser(id);

        if (user.getEmail() != null) {
            userChange.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userChange.setName(user.getName());
        }

        return mapToUserDto(userRepository.save(userChange));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Delete user by id {}", id);
        checkUser(id);
        userRepository.deleteById(id);
    }

    private User checkUser(Long userChange) {
        return userRepository.findById(userChange).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
