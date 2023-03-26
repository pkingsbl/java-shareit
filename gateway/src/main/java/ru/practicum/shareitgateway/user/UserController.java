package ru.practicum.shareitgateway.user;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.user.dto.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable @Min(value = 1, message = "id должен быть больше 0") Long id) {
        return userClient.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody UserDto userDto) {
        return userClient.add(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> change(@PathVariable @Min(value = 1, message = "id должен быть больше 0") Long id,
                @RequestBody UserDto userDto) {
        return userClient.change(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @Min(value = 1, message = "id должен быть больше 0") Long id) {
        userClient.deleteById(id);
    }

}
