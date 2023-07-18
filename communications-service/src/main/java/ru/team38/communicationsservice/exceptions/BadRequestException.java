package ru.team38.communicationsservice.exceptions;

public class BadRequestException extends StatusException {
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
