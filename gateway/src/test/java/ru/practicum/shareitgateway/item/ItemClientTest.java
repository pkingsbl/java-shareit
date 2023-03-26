package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitgateway.exception.ValidationException;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemClientTest {

    private final ItemClient itemClient;

    @Test
    void testGetAllIfFromIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemClient.getAll(1L, -1, 10));

        Assertions.assertEquals("Индекс первого элемента должен быть больше или равен 0", exception.getMessage());
    }

    @Test
    void testGetAllIfSizeIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemClient.getAll(1L, 1, 0));

        Assertions.assertEquals("Количество элементов для отображения должно быть больше 0", exception.getMessage());
    }

    @Test
    void testGetSearchIfFromIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemClient.getSearch(1L, "text", -1, 10));

        Assertions.assertEquals("Индекс первого элемента должен быть больше или равен 0", exception.getMessage());
    }

    @Test
    void testGetSearchIfSizeIncorrect() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemClient.getSearch(1L, "text", 1, 0));

        Assertions.assertEquals("Количество элементов для отображения должно быть больше 0", exception.getMessage());
    }

}