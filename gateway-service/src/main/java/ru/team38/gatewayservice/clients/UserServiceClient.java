package ru.team38.gatewayservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.*;

@FeignClient(name = "user-service", url = "${spring.services.user.url}")
public interface UserServiceClient {

    @PostMapping("/api/v1/auth/register")
    ResponseEntity<String> register(@RequestBody RegisterDto registerDto);

    @PostMapping("/api/v1/auth/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginForm loginForm);

    @PostMapping("/api/v1/auth/logout")
    ResponseEntity<String> logout();

    @GetMapping("/api/v1/auth/captcha")
    ResponseEntity<CaptchaDto> getCaptcha();

    @GetMapping("/api/v1/friends/count")
    ResponseEntity<Integer> getIncomingFriendRequestsCount();

    @GetMapping("/api/v1/account/me")
    ResponseEntity<AccountDto> getAccount();

    @PutMapping("/api/v1/account/me")
    ResponseEntity<AccountDto> updateAccount(@RequestBody AccountDto account);

    @GetMapping("/api/v1/account/{id}")
    ResponseEntity<AccountDto> getAccountById(@PathVariable long id);

    @GetMapping("/api/v1/friends")
    ResponseEntity<PageFriendShortDto> getFriendsByParameters(@RequestParam FriendSearchDto friendSearchDto, @RequestParam PageDto pageDto);
}