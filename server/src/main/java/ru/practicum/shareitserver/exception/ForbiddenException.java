package ru.practicum.shareitserver.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String s) {
        super(s);
    }
}
