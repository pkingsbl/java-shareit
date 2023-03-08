package ru.practicum.shareit.booking.service;

import java.time.LocalDate;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private BookingRepository bookingRepository = mock(BookingRepository.class);
    private BookingService bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);

    private User userReturn = new User(1L, "name", "email@email.com");
    private User ownerReturn = new User(2L, "name", "email@email.com");
    private Item itemReturn = Item.builder().id(1L).owner(ownerReturn).available(true).build();
    private Booking bookingReturn = Booking.builder()
            .id(1L)
            .start(LocalDate.now().atStartOfDay().plusDays(1))
            .end(LocalDate.now().atStartOfDay().plusDays(2))
            .item(itemReturn)
            .build();

    @BeforeEach
    public void beforeEach() {


    }

    @Test
    void add() {


        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userReturn));

        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemReturn));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingReturn);

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDate.now().atStartOfDay().plusDays(1))
                .end(LocalDate.now().atStartOfDay().plusDays(2))
                .itemId(1L)
                .build();

        Booking booking = bookingService.add(userReturn.getId(), bookingDto);
        System.out.println(booking);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userReturn.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(itemReturn.getId());

    }

    @Test
    void approve() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAllByUser() {
    }

    @Test
    void findAllByOwner() {
    }
}