package ru.team38.gatewayservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.*;
import ru.team38.gatewayservice.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        log.info("Executing registration request");
        return userService.register(registerDto);
    }

    @PostMapping("/api/v1/auth/login")
    public LoginResponse login(@RequestBody @Valid LoginForm loginForm) {
        log.info("Executing login request");
        return userService.login(loginForm);
    }

    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<String> logout() {
        log.info("Executing logout request");
        return userService.logout();
    }

    @PostMapping("/api/v1/auth/refresh")
    public LoginResponse refresh(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        log.info("Executing refresh request");
        return userService.refresh(refreshTokenRequest);
    }

    @GetMapping("/api/v1/auth/captcha")
    public CaptchaDto getCaptcha() {
        log.info("Executing getCaptcha request");
        return userService.getCaptcha();
    }

    @GetMapping("/api/v1/friends/count")
    public Integer getIncomingFriendRequests() {
        log.info("Executing getIncomingFriendRequests request");
        return userService.getIncomingFriendRequestsCount();
    }

    @GetMapping("/api/v1/friends")
    public PageFriendShortDto getFriendsByParameters(FriendSearchDto friendSearchDto, PageDto pageDto) {
        log.info("Executing getFriends request");
        return userService.getFriendsByParameters(friendSearchDto, pageDto);
    }
}