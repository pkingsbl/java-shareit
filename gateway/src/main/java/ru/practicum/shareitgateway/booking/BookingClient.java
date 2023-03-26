package ru.practicum.shareitgateway.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareitgateway.booking.dto.BookingDto;
import ru.practicum.shareitgateway.booking.dto.State;
import ru.practicum.shareitgateway.client.BaseClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.exception.ValidationException;
import java.util.Map;

@Slf4j
@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(Long userId, BookingDto bookingDto) {
        log.info("User: {}. Add booking {}", userId, bookingDto.toString());
        checkDate(bookingDto);

        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> approve(Long bookingId, Long userId, Boolean approved) {
        log.info("User: {}. Add approve {} for booking {}", userId, approved, bookingId);

        Map<String, Object> parameters = Map.of(
                "approved", approved
        );

        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> findById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllByUser(Long userId, String state, int from, int size) {
        checkParam(from, size);
        State stateBooking = State.from(state)
                .orElseThrow(() -> new ValidationException("Unknown state: UNSUPPORTED_STATUS"));

        Map<String, Object> parameters = Map.of(
                "state", stateBooking.name(),
                "from", from,
                "size", size
        );

        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllByOwner(Long userId, String state, int from, int size) {
        checkParam(from, size);
        State stateBooking = State.from(state)
                .orElseThrow(() -> new ValidationException("Unknown state: UNSUPPORTED_STATUS"));

        Map<String, Object> parameters = Map.of(
                "state", stateBooking.name(),
                "from", from,
                "size", size
        );

        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    private void checkDate(BookingDto booking) {
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new ValidationException("Поле даты бронирования не может быть пустым");
        }
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new ValidationException("Дата окончания бронирования раньше начала бронирования");
        }
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
