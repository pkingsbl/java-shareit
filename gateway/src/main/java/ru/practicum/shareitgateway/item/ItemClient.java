package ru.practicum.shareitgateway.item;

import java.util.Map;
import ru.practicum.shareitgateway.item.dto.ItemDto;
import ru.practicum.shareitgateway.client.BaseClient;
import ru.practicum.shareitgateway.item.dto.CommentDto;
import ru.practicum.shareitgateway.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> change(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        checkParam(from, size);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getSearch(Long userId, String text, int from, int size) {
        checkParam(from, size);
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public void deleteById(Long userId, Long itemId) {
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> postComment(Long userId, Long itemId, CommentDto comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }

    private void checkParam(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Индекс первого элемента должен быть больше или равен 0");
        }
        if (size < 1) {
            throw new ValidationException("Количество элементов для отображения должно быть больше 0");
        }
    }

}
