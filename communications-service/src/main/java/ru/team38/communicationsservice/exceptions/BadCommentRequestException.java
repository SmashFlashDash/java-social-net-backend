package ru.team38.communicationsservice.exceptions;

public class BadCommentRequestException extends RuntimeException {
    public BadCommentRequestException(String message) {
        super(message);
    }
}
