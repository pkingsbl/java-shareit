package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 * PATCH /items/{itemId}
 * GET /items/{itemId}
 * /items/search?text={text}, в text передаётся текст для поиска. Проверьте, что поиск возвращает только доступные для аренды вещи.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto change(@RequestHeader("X-Sharer-User-Id") Long userId
            , @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId
            , @RequestBody ItemDto itemDto) {
        return itemService.change(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId
            , @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping()
    public Collection<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearch(@RequestHeader("X-Sharer-User-Id") Long userId
            , @RequestParam(defaultValue = "unread") String text) {
        return itemService.getSearch(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@RequestHeader("X-Sharer-User-Id") Long userId
            , @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId) {
        itemService.deleteById(userId, itemId);
    }

}
