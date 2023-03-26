package ru.practicum.shareitserver.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long ownerId, PageRequest of);

    Collection<Item> findAllByNameOrDescriptionContainingIgnoreCase(String name, String desc, PageRequest of);

    Collection<Item> findAllByRequestId(Long id);
}
