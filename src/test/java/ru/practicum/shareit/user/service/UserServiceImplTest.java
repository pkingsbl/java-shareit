package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.Collection;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService service;
    private final UserRepository userRepository;

    @Test
    void getAll() {
        List<User> users = List.of(
            new User(null, "Name1", "some1@email.com"),
            new User(null, "Name2", "some2@email.com"),
            new User(null, "Name3", "some3@email.com")
        );
        userRepository.saveAll(users);
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
    }

    @Test
    void getById() {
        User user = new User(null, "NameById", "someById@email.com");
        Long idUser = userRepository.save(user).getId();
        UserDto userDto =  service.getById(idUser);

        assertThat(userDto.getId(), equalTo(idUser));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void add() {
        User user = new User(null, "Name", "some@email.com");
        UserDto userDto =  service.add(user);

        assertThat(userDto.getId(), notNullValue());
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void change() {
        User userBeforeChange = new User(null, "NameBeforeChange", "someBeforeChange@email.com");
        Long idUser = userRepository.save(userBeforeChange).getId();
        User userAfterChange = new User(null, "NameAfterChange", "someAfterChange@email.com");
        UserDto userDto =  service.change(idUser, userAfterChange);

        assertThat(userDto.getId(), equalTo(idUser));
        assertThat(userDto.getName(), equalTo(userAfterChange.getName()));
        assertThat(userDto.getEmail(), equalTo(userAfterChange.getEmail()));
    }

    @Test
    void deleteById() {
        User user = new User(null, "NameById", "someById@email.com");
        Long idUser = userRepository.save(user).getId();
        UserDto userDtoBeforeDelete =  service.getById(idUser);
        assertThat(userDtoBeforeDelete, notNullValue());

        service.deleteById(idUser);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> service.getById(idUser));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }
}