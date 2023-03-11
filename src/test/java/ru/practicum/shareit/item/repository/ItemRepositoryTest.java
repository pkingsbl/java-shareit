package ru.practicum.shareit.item.repository;

import java.util.Collection;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private final User owner = new User(null, "Owner", "owner@email.com");
    private final User requestor = new User(null, "Booker", "booker@email.com");

    private final ItemRequest itemRequest = ItemRequest.builder()
            .description("Item")
            .created(LocalDateTime.now())
            .requestor(requestor)
            .build();
    private final Item item = Item.builder()
            .id(null)
            .available(true)
            .name("Item")
            .description("Item for you")
            .owner(owner)
            .request(itemRequest)
            .build();

    @BeforeEach
    void beforeEach() {
        userRepository.save(owner);
        userRepository.save(requestor);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerId() {
        Collection<Item> items = itemRepository.findAllByOwnerId(owner.getId(), PageRequest.of(0, 10));

        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(items.size(), 1);
        assertTrue(items.contains(item));
    }

    @Test
    void findAllByNameOrDescriptionContainingIgnoreCase() {
        Collection<Item> items = itemRepository.findAllByNameOrDescriptionContainingIgnoreCase("for you", "for you", PageRequest.of(0, 10));

        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(items.size(), 1);
        assertTrue(items.contains(item));
    }

    @Test
    void findAllByRequestId() {
        Collection<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());

        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(items.size(), 1);
        assertTrue(items.contains(item));
    }
}