package ru.team38.gatewayservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.*;
import ru.team38.gatewayservice.service.UserService;

import java.util.List;
import java.util.UUID;

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
    public CountDto getIncomingFriendRequests() {
        log.info("Executing getIncomingFriendRequests request");
        return userService.getIncomingFriendRequestsCount();
    }

    @GetMapping("/api/v1/friends")
    public PageFriendShortDto getFriendsByParameters(FriendSearchDto friendSearchDto, PageDto pageDto) {
        log.info("Executing getFriends request");
        return userService.getFriendsByParameters(friendSearchDto, pageDto);
    }

    @GetMapping("/api/v1/friends/recommendations")
    public List<FriendShortDto> getFriendsRecommendations(FriendSearchDto friendSearchDto) {
        log.info("Executing getFriendsRecommendations request");
        return userService.getFriendsRecommendations(friendSearchDto);
    }

    @PutMapping("/api/v1/friends/block/{id}")
    public ResponseEntity<FriendShortDto> blockAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.blockAccount(id));
    }

    @PutMapping("/api/v1/friends/unblock/{id}")
    public ResponseEntity<FriendShortDto> unblockAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.unblockAccount(id));
    }

    @GetMapping("/api/v1/geo/country")
    public ResponseEntity<List<CountryDto>> getCountries() {
        List<CountryDto> countries = userService.getCountries().getBody();
        return ResponseEntity.status(HttpStatus.OK).body(countries);
    }

    @GetMapping("/api/v1/geo/country/{countryId}/city")
    public ResponseEntity<List<CityDto>> getCitiesByCountryId(@PathVariable String countryId) {
        List<CityDto> cities = userService.getCitiesByCountryId(countryId).getBody();
        return ResponseEntity.ok(cities);
    }
}