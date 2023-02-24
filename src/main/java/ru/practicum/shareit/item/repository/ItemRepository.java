package ru.practicum.shareit.item.repository;

import java.util.Collection;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long ownerId);

    Collection<Item> findAllByNameOrDescriptionContainingIgnoreCase(String name, String desc);

}
