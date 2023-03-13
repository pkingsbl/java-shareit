package ru.practicum.shareit.request;

import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto add(@RequestHeader(HEADER_ID) Long userId, @Valid @RequestBody ItemRequest itemRequest) {
        return itemRequestService.add(userId, itemRequest);
    }

    @GetMapping()
    public Collection<ItemRequestDto> getAllOwner(@RequestHeader(HEADER_ID) Long userId) {
        return itemRequestService.getAllOwner(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAll(@RequestHeader(HEADER_ID) Long userId,
                    @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "1")  Integer size) {
        return itemRequestService.getAllFrom(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(HEADER_ID) Long userId,
                                    @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

}
