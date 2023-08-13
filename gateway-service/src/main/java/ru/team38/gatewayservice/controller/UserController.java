package ru.team38.gatewayservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.account.*;
import ru.team38.common.dto.friend.FriendSearchDto;
import ru.team38.common.dto.friend.FriendShortDto;
import ru.team38.common.dto.geography.CityDto;
import ru.team38.common.dto.geography.CountryDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.common.dto.other.PageDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.gatewayservice.service.UserService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerInterface {

    private final UserService userService;

    @Override
    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        log.info("Executing registration request");
        return userService.register(registerDto);
    }

    @Override
    @PostMapping("/api/v1/auth/login")
    public LoginResponse login(@RequestBody @Valid LoginForm loginForm) {
        log.info("Executing login request");
        return userService.login(loginForm);
    }

    @Override
    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<String> logout() {
        log.info("Executing logout request");
        return userService.logout();
    }

    @Override
    @PostMapping("/api/v1/auth/change-email-link")
    public ResponseEntity<String> changeEmail(@RequestBody EmailDto emailDto) {
        log.info("Executing changeEmail request");
        return userService.changeEmail(emailDto);
    }

    @Override
    @PostMapping("/api/v1/auth/change-password-link")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto passwordDto) {
        log.info("Executing changePassword request");
        return userService.changePassword(passwordDto);
    }

    @Override
    @PostMapping("/api/v1/auth/refresh")
    public LoginResponse refresh(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        log.info("Executing refresh request");
        return userService.refresh(refreshTokenRequest);
    }

    @Override
    @GetMapping("/api/v1/auth/captcha")
    public CaptchaDto getCaptcha() {
        log.info("Executing getCaptcha request");
        return userService.getCaptcha();
    }

    @Override
    @PostMapping("/api/v1/auth/password/recovery/")
    public ResponseEntity<String> recoverPassword(@RequestBody EmailDto emailDto) {
        return userService.recoverPassword(emailDto);
    }

    @Override
    @PostMapping("/api/v1/auth/password/recovery/{linkId}")
    public ResponseEntity<String> setNewPassword(@PathVariable String linkId,
                                                 @RequestBody NewPasswordDto newPasswordDto) {
        return userService.setNewPassword(linkId, newPasswordDto);
    }

    @Override
    @GetMapping("/api/v1/friends/count")
    public CountDto getIncomingFriendRequests() {
        log.info("Executing getIncomingFriendRequests request");
        return userService.getIncomingFriendRequestsCount();
    }

    @Override
    @GetMapping("/api/v1/friends")
    public PageResponseDto<Object> getFriendsByParameters(FriendSearchDto friendSearchDto, PageDto pageDto) {
        log.info("Executing getFriends request");
        return userService.getFriendsByParameters(friendSearchDto, pageDto);
    }

    @Override
    @GetMapping("/api/v1/friends/recommendations")
    public List<FriendShortDto> getFriendsRecommendations(FriendSearchDto friendSearchDto) {
        log.info("Executing getFriendsRecommendations request");
        return userService.getFriendsRecommendations(friendSearchDto);
    }

    @Override
    @PutMapping("/api/v1/friends/block/{id}")
    public ResponseEntity<FriendShortDto> blockAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.blockAccount(id));
    }

    @Override
    @PutMapping("/api/v1/friends/unblock/{id}")
    public ResponseEntity<FriendShortDto> unblockAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.unblockAccount(id));
    }

    @Override
    @PostMapping("/api/v1/friends/{id}/request")
    public ResponseEntity<FriendShortDto> makeFriendRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.makeFriendRequest(id));
    }

    @Override
    @PutMapping("/api/v1/friends/{id}/approve")
    public ResponseEntity<FriendShortDto> approveFriendRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.approveFriendRequest(id));
    }

    @Override
    @DeleteMapping("/api/v1/friends/{id}")
    public ResponseEntity<Void> deleteRelationship(@PathVariable UUID id) {
        userService.deleteRelationship(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @PostMapping("/api/v1/friends/subscribe/{id}")
    public ResponseEntity<FriendShortDto> getSubscription(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getSubscription(id));
    }


    @Override
    @PutMapping("/load")
    public ResponseEntity<String> loadGeoData() {
        return ResponseEntity.ok("Обновление стран автоматизировано и ручной перезагрузки не требует");
    }

    @Override
    @GetMapping("/api/v1/geo/country")
    public ResponseEntity<List<CountryDto>> getCountries() {
        List<CountryDto> countries = userService.getCountries().getBody();
        return ResponseEntity.status(HttpStatus.OK).body(countries);
    }

    @Override
    @GetMapping("/api/v1/geo/country/{countryId}/city")
    public ResponseEntity<List<CityDto>> getCitiesByCountryId(@PathVariable String countryId) {
        List<CityDto> cities = userService.getCitiesByCountryId(countryId).getBody();
        return ResponseEntity.ok(cities);
    }
}
