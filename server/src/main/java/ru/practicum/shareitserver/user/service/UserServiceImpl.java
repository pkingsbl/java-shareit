package ru.practicum.shareitserver.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.Collection;

import static ru.practicum.shareitserver.user.UserMapper.mapToUserDto;

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
