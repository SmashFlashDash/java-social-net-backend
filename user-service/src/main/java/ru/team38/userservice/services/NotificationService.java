package ru.team38.userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.AccountDto;
import ru.team38.common.dto.CountDto;
import ru.team38.common.dto.notification.NotificationCountDto;
import ru.team38.userservice.data.repositories.NotificationRepository;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final AccountService accountService;
    private final NotificationRepository notificationRepository;

    @LoggingMethod
    public NotificationCountDto getNotificationsCount() {
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        Integer count = notificationRepository.getNotificationsCountByUserId(accountDto.getId());
        return new NotificationCountDto(ZonedDateTime.now(), new CountDto(count));
    }
}
