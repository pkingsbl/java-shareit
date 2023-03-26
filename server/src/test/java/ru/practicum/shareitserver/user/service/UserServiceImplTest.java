package ru.practicum.shareitserver.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplTest {

    @MockBean
    private final UserRepository userRepository;
    @InjectMocks
    private final UserService service;

    private final List<User> users = List.of(
            new User(1L, "Name1", "some1@email.com"),
            new User(2L, "Name2", "some2@email.com"),
            new User(3L, "Name3", "some3@email.com")
    );
    private final User userChange = new User(1L, "NameAfterChange", "someAfterChange@email.com");


    @BeforeEach
    void setUp() {
        when(userRepository.findAll())
                .thenReturn(users);
        when(userRepository.findById(users.get(0).getId()))
                .thenReturn(Optional.ofNullable(users.get(0)));
        when(userRepository.findById(users.get(2).getId()))
                .thenReturn(Optional.ofNullable(users.get(2)));
        when(userRepository.save(users.get(0)))
                .thenReturn(users.get(0));
        when(userRepository.save(userChange))
                .thenReturn(userChange);
    }

    @Test
    void getAll() {
        Collection<UserDto> userDtos =  service.getAll();

        assertThat(userDtos, is(not(empty())));
        assertThat(userDtos, hasSize(users.size()));
        for (User user : users) {
            assertThat(userDtos, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getById() {
        UserDto userDto =  service.getById(users.get(0).getId());

        assertThat(userDto.getId(), equalTo(users.get(0).getId()));
        assertThat(userDto.getName(), equalTo(users.get(0).getName()));
        assertThat(userDto.getEmail(), equalTo(users.get(0).getEmail()));
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(users.get(0).getId());
    }

    @Test
    void testAdd() {
        UserDto userDto =  service.add(users.get(0));

        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(users.get(0).getName()));
        assertThat(userDto.getEmail(), equalTo(users.get(0).getEmail()));
        Mockito.verify(userRepository, Mockito.times(1))
                .save(users.get(0));
    }

    @Test
    void testChange() {
        UserDto userDto =  service.change(users.get(0).getId(), userChange);

        assertThat(userDto.getId(), equalTo(userChange.getId()));
        assertThat(userDto.getName(), equalTo(userChange.getName()));
        assertThat(userDto.getEmail(), equalTo(userChange.getEmail()));
        Mockito.verify(userRepository, Mockito.times(1))
                .save(userChange);
    }

    @Test
    void testDeleteById() {
        service.deleteById(users.get(2).getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(users.get(2).getId());
    }

    @Test
    void testDeleteByIdIfUserNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> service.deleteById(404L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }
}