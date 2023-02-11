package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public Collection<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable @Min(value = 1, message = "id должен быть больше 0") Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody User user) {
        return userService.add(user);
    }

    @PatchMapping("/{id}")
    public UserDto change(@PathVariable @Min(value = 1, message = "id должен быть больше 0") Long id,
                @RequestBody User user) {
        return userService.change(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @Min(value = 1, message = "id должен быть больше 0") Long id) {
        userService.deleteById(id);
    }

}
