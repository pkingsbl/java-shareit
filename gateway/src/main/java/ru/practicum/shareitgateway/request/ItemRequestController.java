package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.request.dto.ItemRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(HEADER_ID) Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllOwner(@RequestHeader(HEADER_ID) Long userId) {
        return itemRequestClient.getAllOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER_ID) Long userId,
                    @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "1")  Integer size) {
        return itemRequestClient.getAllFrom(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER_ID) Long userId,
                                    @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }

}
