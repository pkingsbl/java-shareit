package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitgateway.exception.ValidationException;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestClientTest {

    private final ItemRequestClient itemRequestClient;

    @Test
    void testGetAllFromIfFromIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestClient.getAllFrom(1L, -1, 10));

        Assertions.assertEquals("Индекс первого элемента должен быть больше или равен 0", exception.getMessage());
    }

    @Test
    void testGetAllFromIfSizeIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestClient.getAllFrom(1L, 1, 0));

        Assertions.assertEquals("Количество элементов для отображения должно быть больше 0", exception.getMessage());
    }

}