package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> getAll();

    User getById(Long id);

    User add(User user);

    User change(Long id, User user) ;

    User deleteById(Long id);
}
