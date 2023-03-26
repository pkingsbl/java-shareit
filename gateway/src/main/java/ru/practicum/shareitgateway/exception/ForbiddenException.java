package ru.practicum.shareitgateway.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String s) {
        super(s);
    }
}
