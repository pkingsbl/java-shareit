package ru.practicum.shareitserver.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private final LocalDateTime dateTime = LocalDateTime.now();
    private final User owner = new User(null, "Owner", "owner@email.com");
    private final User requestor = new User(null, "Booker", "booker@email.com");

    private final ItemRequest itemRequest = ItemRequest.builder()
            .description("Item")
            .created(dateTime)
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
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindAllByOwnerId() {
        Collection<Item> items = itemRepository.findAllByOwnerId(owner.getId(), PageRequest.of(0, 10));
        checkAsserts(items);
    }

    @Test
    void testFindAllByNameOrDescriptionContainingIgnoreCase() {
        Collection<Item> items = itemRepository.findAllByNameOrDescriptionContainingIgnoreCase("for you", "for you", PageRequest.of(0, 10));
        checkAsserts(items);
    }

    @Test
    void testFindAllByRequestId() {
        Collection<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        checkAsserts(items);
    }

    private void checkAsserts(Collection<Item> items) {
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(items.size(), 1);
        assertTrue(items.contains(item));
    }

}