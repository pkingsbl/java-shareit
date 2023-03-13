package ru.practicum.shareit.request.repository;

import java.util.Collection;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedAsc(Long userId);

    Collection<ItemRequest> findAllByRequestorNotLike(User user, PageRequest of);
}
