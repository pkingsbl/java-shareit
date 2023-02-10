package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private Long idUser = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User add(User user) {
        user.setId(idUser);
        users.put(idUser, user);
        idUser++;
        return user;
    }

    @Override
    public User change(Long id, User user) {
        if (users.containsKey(id)) {
            if(user.getEmail() != null) {
                users.get(id).setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                users.get(id).setName(user.getName());
            }
            return users.get(id);
        }
        return null;
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public User deleteById(Long id) {
        return users.remove(id);
    }
}

