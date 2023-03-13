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

    private final LocalDateTime dateTime = LocalDateTime.now();
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
            .start(dateTime.plusDays(1))
            .end(dateTime.plusDays(2))
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
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindAllByBookerId() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerId(booker.getId(), PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByBookerIdAndStartBeforeAndEndAfter() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(), dateTime.plusDays(2), dateTime, PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByBookerIdAndEndBefore() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), dateTime.plusDays(3), PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByBookerIdAndStartAfter() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), dateTime, PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByBookerIdAndStatusApproved() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.APPROVED, PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByItemOwnerId() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerId(owner.getId(), PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByItemOwnerIdAndStartBeforeAndEndAfter() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(owner.getId(), dateTime.plusDays(2), dateTime, PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByItemOwnerIdAndEndBefore() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(owner.getId(), dateTime.plusDays(3), PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByItemOwnerIdAndStartAfter() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(owner.getId(), dateTime, PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByItemOwnerIdAndStatusApproved() {
        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(owner.getId(), Status.APPROVED, PageRequest.of(0, 10));
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByItemIdAndStatusOrderByStartAscIfStatusApproved() {
        Collection<Booking> bookings = bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(item.getId(), Status.APPROVED);
        checkAsserts(bookings);
    }

    @Test
    void testFindAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBeforeIfStatusApproved() {
        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(booker.getId(), item.getId(), Status.APPROVED, dateTime.plusDays(3));
        checkAsserts(bookings);
    }

    private void checkAsserts(Collection<Booking> bookings) {
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.size(), 1);
        assertTrue(bookings.contains(booking));
    }

}