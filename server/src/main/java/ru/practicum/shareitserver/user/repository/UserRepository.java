package ru.practicum.shareitserver.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
