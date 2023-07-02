package ru.team38.communicationsservice.exceptions;

public class UnreadMessagesCountNotFoundExceptions extends Exception{
    public UnreadMessagesCountNotFoundExceptions() {
        super("Unread messages count not found");
    }
}
