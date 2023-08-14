package ru.team38.userservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.notification.*;
import ru.team38.common.dto.account.AccountDto;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.dto.notification.NotificationUpdateDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.dto.other.PageableDto;
import ru.team38.common.dto.other.SortDto;
import ru.team38.userservice.data.repositories.AccountRepository;
import ru.team38.userservice.data.repositories.NotificationRepository;
import ru.team38.userservice.security.jwt.JwtService;

import java.time.ZonedDateTime;
import java.util.*;
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final AccountService accountService;
    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    @LoggingMethod
    public DataTimestampDto getNotificationsCount() {
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        Integer count = notificationRepository.getNotificationsCountByAccountId(accountDto.getId());
        return new DataTimestampDto(ZonedDateTime.now(), new CountDto(count));
    }
    @LoggingMethod
    public PageResponseDto<DataTimestampDto> getNotificationsPage(String lang, Integer size) {
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        Integer count = notificationRepository.getNotificationsCountByAccountId(accountDto.getId());
        List<NotificationDto> notifications = notificationRepository.getNotificationsByAccountId(accountDto.getId(), size);
        List<DataTimestampDto> notificationsByTimestamp = notifications.stream().map(record -> {
             if(record.getNotificationType().equals(NotificationTypeEnum.FRIEND_BIRTHDAY)){
                String birthdayMessage = getBirthdayMessageByLanguage(lang);
                record.setContent("\uD83C\uDF89\uD83C\uDF82 " + birthdayMessage + " \uD83C\uDF88\uD83C\uDF1F");
            }
            DataTimestampDto data = new DataTimestampDto();
            data.setTimestamp(ZonedDateTime.now());
            data.setData(record);
            return data;
        }).toList();
        return makePageDto(notificationsByTimestamp, count, size);
    }

    @LoggingMethod
    public void readAllNotifications(Integer size) {
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        notificationRepository.updateNotificationsReadByAccountId(accountDto.getId(), size);
    }

    private PageResponseDto<DataTimestampDto> makePageDto(List<DataTimestampDto> notifications, Integer count, Integer size) {
        int number= 0;
        int offset = 0;
        SortDto sortDto = new SortDto(false, true, notifications.isEmpty());
        return PageResponseDto.<DataTimestampDto>builder()
                .content(notifications)
                .totalElements(count)
                .totalPages((int) Math.ceil(count / (float) size))
                .number(number)
                .numberOfElements(notifications.size())
                .size(size)
                .sort(sortDto)
                .first(true)
                .last(count <= notifications.size())
                .pageable(new PageableDto(sortDto, number, notifications.size(), offset, false, false))
                .empty(notifications.isEmpty())
                .build();
    }

    @LoggingMethod
    public NotificationSettingDto getNotificationSetting(HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        return accountRepository.getNotificationSetting(username);
    }

    @LoggingMethod
    public NotificationSettingDto updateNotificationSetting(HttpServletRequest request,
                                                            NotificationUpdateDto notificationUpdateDto) {
        String username = getUsernameFromRequest(request);
        return accountRepository.updateNotificationSetting(username, notificationUpdateDto.getNotificationType(),
                                                            notificationUpdateDto.getEnable());
    }

    @LoggingMethod
    public NotificationSettingDto setNotificationSetting(UUID accountId) {
        return accountRepository.setNotificationSetting(accountId);
    }

    private String getUsernameFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return jwtService.getUsername(bearerToken.substring(7));
    }

    private String getBirthdayMessageByLanguage(String languageCode) {
        if (languageCode.equalsIgnoreCase("RU")) {
            return BirthdayMessage.RU.getMessage();
        } else {
            return BirthdayMessage.EN.getMessage();
        }
    }
}
