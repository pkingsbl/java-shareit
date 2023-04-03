package ru.practicum.shareitgateway.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleValidationException() {
        ValidationException e = new ValidationException("Валидация не пройдена: ");
        ErrorResponse errorResponse = handler.handleValidation(e);
        assertNotNull(errorResponse);
        assertTrue(errorResponse.getError().contains("Валидация не пройдена: "));
    }

}