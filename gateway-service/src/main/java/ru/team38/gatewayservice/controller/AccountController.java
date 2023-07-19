package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.AccountDto;
import ru.team38.common.dto.PageAccountDto;
import ru.team38.common.dto.AccountSearchDto;
import ru.team38.common.dto.PageDto;
import ru.team38.gatewayservice.service.UserService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(userService.createAccount(accountDto));
    }

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

    @GetMapping("/search")
    public PageAccountDto findAccount(AccountSearchDto accountSearchDto, PageDto pageDto) {
        log.info("Executing findAccount request");
        return userService.findAccount(accountSearchDto, pageDto);
    }
}
