package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.account.AccountDto;
import ru.team38.common.dto.account.AccountSearchDto;
import ru.team38.common.dto.other.PageDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.gatewayservice.service.UserService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController implements AccountControllerInterface {
    private final UserService userService;

    @Override
    @PostMapping("/")
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(userService.createAccount(accountDto));
    }

    @Override
    @GetMapping("/me")
    public AccountDto getAccount() {
        return userService.getAccount();
    }

    @Override
    @PutMapping("/me")
    public AccountDto updateAccount(@RequestBody AccountDto account) {
        return userService.updateAccount(account);
    }

    @Override
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAccount() {
        return userService.deleteAccount();
    }

    @Override
    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable UUID id) {
        return userService.getAccountById(id);
    }

    @Override
    @GetMapping("/search")
    public PageResponseDto<AccountDto> findAccount(AccountSearchDto accountSearchDto, PageDto pageDto) {
        return userService.findAccount(accountSearchDto, pageDto);
    }

    @Override
    @GetMapping("/search/statusCode")
    public PageResponseDto<AccountDto> findAccountByStatusCode(AccountSearchDto accountSearchDto, PageDto pageDto) {
        return userService.findAccountByStatusCode(accountSearchDto, pageDto);
    }
}
