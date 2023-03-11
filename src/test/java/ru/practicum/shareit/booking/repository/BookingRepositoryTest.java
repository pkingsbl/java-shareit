package ru.practicum.shareit.booking.repository;

import java.util.Collection;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final User booker = new User(null, "Booker", "booker@email.com");
    private final User owner = new User(null, "Owner", "owner@email.com");
    private final Item item = Item.builder()
            .id(null)
            .available(true)
            .name("Item")
            .description("Item for you")
            .owner(owner)
            .build();
    private final Booking booking = Booking.builder()
            .id(null)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(item)
            .booker(booker)
            .status(Status.APPROVED)
            .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerId() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerId(booker.getId(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfter() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), LocalDateTime.now().plusDays(3), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByBookerIdAndStatus() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.APPROVED, PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByItemOwnerId() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerId(owner.getId(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfter() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(owner.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByItemOwnerIdAndEndBefore() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(owner.getId(), LocalDateTime.now().plusDays(3), PageRequest.of(0, 10));
        System.out.println(bookings);
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByItemOwnerIdAndStartAfter() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(owner.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(owner.getId(), Status.APPROVED, PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByItemIdAndStatusOrderByStartAsc() {
        Collection<Booking> bookings = bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(item.getId(), Status.APPROVED);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(booker.getId(), item.getId(), Status.APPROVED, LocalDateTime.now().plusDays(3));

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }
}