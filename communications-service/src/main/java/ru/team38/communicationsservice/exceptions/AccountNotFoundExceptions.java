package ru.team38.communicationsservice.exceptions;

public class AccountNotFoundExceptions extends Exception{
    public AccountNotFoundExceptions() {
        super("Account not found");
    }
}
