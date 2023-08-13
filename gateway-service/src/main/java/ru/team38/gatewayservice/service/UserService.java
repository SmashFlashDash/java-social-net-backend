package ru.team38.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.team38.common.dto.account.*;
import ru.team38.common.dto.friend.FriendSearchDto;
import ru.team38.common.dto.friend.FriendShortDto;
import ru.team38.common.dto.geography.CityDto;
import ru.team38.common.dto.geography.CountryDto;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.dto.notification.NotificationUpdateDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.common.dto.other.PageDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.gatewayservice.clients.UserServiceClient;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserServiceClient userServiceClient;

    public ResponseEntity<String> register(RegisterDto registerDto) {
        return userServiceClient.register(registerDto);
    }

    public LoginResponse login(LoginForm loginForm) {
        return userServiceClient.login(loginForm).getBody();
    }

    public ResponseEntity<String> logout() {
        return userServiceClient.logout();
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        return userServiceClient.refresh(request).getBody();
    }

    public CaptchaDto getCaptcha() {
        return userServiceClient.getCaptcha().getBody();
    }

    public CountDto getIncomingFriendRequestsCount() {
        return userServiceClient.getIncomingFriendRequestsCount().getBody();
    }

    public AccountDto createAccount(AccountDto accountDto) {
        return userServiceClient.createAccount(accountDto).getBody();
    }

    public AccountDto getAccount() {
        return userServiceClient.getAccount().getBody();
    }

    public AccountDto updateAccount(AccountDto account) {
        return userServiceClient.updateAccount(account).getBody();
    }

    public AccountDto getAccountById(UUID id) {
        return userServiceClient.getAccountById(id).getBody();
    }

    public DataTimestampDto getNotificationsCount() {
        return userServiceClient.getNotificationsCount().getBody();
    }

    public PageResponseDto<DataTimestampDto> getNotificationsPage(@RequestHeader("x-lang") String lang) {
        return userServiceClient.getNotificationsPage(lang).getBody();
    }

    public String readAllNotifications() {
        return userServiceClient.readAllNotifications().getBody();
    }

    public NotificationSettingDto getNotificationSetting() {
        return userServiceClient.getNotificationSetting().getBody();
    }

    public NotificationSettingDto updateNotificationSetting(NotificationUpdateDto notificationUpdateDto) {
        return userServiceClient.updateNotificationSetting(notificationUpdateDto).getBody();
    }

    public NotificationSettingDto setNotificationSetting(UUID id) {
        return userServiceClient.setNotificationSetting(id).getBody();
    }

    public ResponseEntity<String> deleteAccount() {
        return userServiceClient.deleteAccount();
    }

    public PageResponseDto<Object> getFriendsByParameters(FriendSearchDto friendSearchDto, PageDto pageDto) {
        ResponseEntity<PageResponseDto<Object>> responseEntity = userServiceClient.getFriendsByParameters(
                friendSearchDto.getStatusCode(),
                friendSearchDto.getFirstName(),
                friendSearchDto.getCity(),
                friendSearchDto.getCountry(),
                friendSearchDto.getAgeFrom(),
                friendSearchDto.getAgeTo(),
                pageDto.getPage(),
                pageDto.getSize(),
                pageDto.getSort());
        return responseEntity.getBody();
    }

    public List<FriendShortDto> getFriendsRecommendations(FriendSearchDto friendSearchDto) {
        ResponseEntity<List<FriendShortDto>> responseEntity = userServiceClient.getFriendsRecommendations(
                friendSearchDto.getFirstName(),
                friendSearchDto.getCity(),
                friendSearchDto.getCountry(),
                friendSearchDto.getAgeFrom(),
                friendSearchDto.getAgeTo());
        return responseEntity.getBody();

    }

    public ResponseEntity<List<CountryDto>> getCountries() {
        return userServiceClient.getCountries();
    }

    public ResponseEntity<List<CityDto>> getCitiesByCountryId(String countryId) {
        return userServiceClient.getCitiesByCountryId(countryId);

    }

    public PageResponseDto<AccountDto> findAccount(AccountSearchDto accountSearchDto, PageDto pageDto) {
        ResponseEntity<PageResponseDto<AccountDto>> responseEntity = userServiceClient
                .findAccount(accountSearchDto.getFirstName(),
                        accountSearchDto.getLastName(),
                        accountSearchDto.getAgeFrom(),
                        accountSearchDto.getAgeTo(),
                        accountSearchDto.getCountry(),
                        accountSearchDto.getCity(),
                        accountSearchDto.getAuthor(),
                        accountSearchDto.getIds(),
                        accountSearchDto.isDeleted(),
                        pageDto.getPage(), pageDto.getSize(), pageDto.getSort());
        return responseEntity.getBody();
    }

    public PageResponseDto<AccountDto> findAccountByStatusCode(AccountSearchDto accountSearchDto, PageDto pageDto) {
        ResponseEntity<PageResponseDto<AccountDto>> responseEntity = userServiceClient
                .findAccountByStatusCode(accountSearchDto.getFirstName(),
                        accountSearchDto.getStatusCode(), pageDto.getPage(),
                        pageDto.getSize(), pageDto.getSort());
        return responseEntity.getBody();
    }

    public FriendShortDto blockAccount(UUID id) {
        return userServiceClient.blockAccount(id).getBody();
    }

    public FriendShortDto unblockAccount(UUID id) {
        return userServiceClient.unblockAccount(id).getBody();
    }

    public ResponseEntity<String> recoverPassword(EmailDto emailDto) {
        return userServiceClient.recoverPassword(emailDto);
    }

    public ResponseEntity<String> setNewPassword(String linkId, NewPasswordDto newPasswordDto) {
        return userServiceClient.setNewPassword(linkId, newPasswordDto);
    }

    public FriendShortDto makeFriendRequest(UUID id) {
        return userServiceClient.makeFriendRequest(id).getBody();
    }

    public FriendShortDto approveFriendRequest(UUID id) {
        return userServiceClient.approveFriendRequest(id).getBody();
    }

    public void deleteRelationship(UUID id) {
        userServiceClient.deleteRelationship(id);
    }

    public FriendShortDto getSubscription(UUID id) {
        return userServiceClient.getSubscription(id).getBody();
    }

    public ResponseEntity<String> changePassword(ChangePasswordDto passwordDto) {
        return userServiceClient.changePassword(passwordDto);
    }

    public ResponseEntity<String> changeEmail(EmailDto emailDto) {
        return userServiceClient.changeEmail(emailDto);
    }
}