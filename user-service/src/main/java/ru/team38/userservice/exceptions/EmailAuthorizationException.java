package ru.team38.userservice.exceptions;

public class EmailAuthorizationException extends RuntimeException {
    public EmailAuthorizationException(String message) {
        super(message);
    }
}
