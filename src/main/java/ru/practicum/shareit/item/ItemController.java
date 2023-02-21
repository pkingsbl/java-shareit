package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestHeader(HEADER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto change(@RequestHeader(HEADER_ID) Long userId,
                @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId,
                @RequestBody ItemDto itemDto) {
        return itemService.change(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(HEADER_ID) Long userId,
                @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping()
    public Collection<ItemDto> getAll(@RequestHeader(HEADER_ID) Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearch(@RequestHeader(HEADER_ID) Long userId,
                @RequestParam(defaultValue = "unread") String text) {
        return itemService.getSearch(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@RequestHeader(HEADER_ID) Long userId,
                @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId) {
        itemService.deleteById(userId, itemId);
    }

}
