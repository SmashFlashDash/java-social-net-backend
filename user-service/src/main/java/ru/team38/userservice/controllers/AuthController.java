package ru.team38.userservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.CaptchaDto;
import ru.team38.common.dto.LoginForm;
import ru.team38.common.dto.LoginResponse;
import ru.team38.common.dto.RegisterDto;
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
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginForm loginForm) {
        return ResponseEntity.ok(authService.login(loginForm));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().body("Успешный логаут");

    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDto> getCaptcha() throws CaptchaCreationException {
        return ResponseEntity.ok().body(captchaService.createCaptcha());
    }
}
