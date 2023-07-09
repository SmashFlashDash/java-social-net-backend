package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.AccountDto;
import ru.team38.gatewayservice.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @GetMapping("/me")
    public AccountDto getAccount() {
        return userService.getAccount();
    }

    @PutMapping("/me")
    public AccountDto updateAccount(@RequestBody AccountDto account) {
        return userService.updateAccount(account);
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAccount() {
        return userService.deleteAccount();
    }

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable UUID id) {
        return userService.getAccountById(id);
    }
}
