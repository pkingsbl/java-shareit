package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.item.dto.CommentDto;
import ru.practicum.shareitgateway.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;
    private static final String HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HEADER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> changeItem(@RequestHeader(HEADER_ID) Long userId,
                    @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId,
                    @RequestBody ItemDto itemDto) {
        return itemClient.change(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByIdItem(@RequestHeader(HEADER_ID) Long userId,
                    @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItem(@RequestHeader(HEADER_ID) Long userId,
                    @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "5")  Integer size) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItem(@RequestHeader(HEADER_ID) Long userId,
                    @RequestParam(defaultValue = "unread") String text,
                    @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "5")  Integer size) {
        return itemClient.getSearch(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@RequestHeader(HEADER_ID) Long userId,
                    @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId) {
        itemClient.deleteById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER_ID) Long userId,
                    @PathVariable @Min(value = 1, message = "id должен быть больше 0") Long itemId,
                    @Valid @RequestBody CommentDto comment) {
        return itemClient.postComment(userId, itemId, comment);
    }

}
