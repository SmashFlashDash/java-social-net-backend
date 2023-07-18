package ru.team38.communicationsservice.exceptions;

public class StatusException extends Exception{
    public StatusException(String message) {
        super(message);
    }
    public StatusException(String message, Throwable ex) {
        super(message, ex);
    }
}
