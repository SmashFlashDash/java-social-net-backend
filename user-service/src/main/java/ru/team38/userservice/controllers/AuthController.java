package ru.team38.userservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.*;
import ru.team38.userservice.exceptions.CaptchaCreationException;
import ru.team38.userservice.exceptions.InvalidCaptchaException;
import ru.team38.userservice.services.AuthService;
import ru.team38.userservice.services.CaptchaService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CaptchaService captchaService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) throws InvalidCaptchaException {
        if (captchaService.validateCaptcha(registerDto.getCaptchaSecret(), registerDto.getCaptchaCode())) {
            authService.register(registerDto);
            return ResponseEntity.ok("Регистрация прошла успешно");
        } else {
            throw new InvalidCaptchaException("Invalid or expired captcha");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(HttpServletRequest request, @RequestBody LoginForm loginForm) {
        return ResponseEntity.ok(authService.login(request, loginForm));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().body("Успешный логаут");

    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshTokenRequest.getRefreshToken()));
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDto> getCaptcha() throws CaptchaCreationException {
        return ResponseEntity.ok().body(captchaService.createCaptcha());
    }
}
