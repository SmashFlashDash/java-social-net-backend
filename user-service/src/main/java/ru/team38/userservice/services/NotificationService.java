package ru.team38.userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.AccountDto;
import ru.team38.common.dto.CountDto;
import ru.team38.common.dto.comment.PageableDto;
import ru.team38.common.dto.comment.SortDto;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.common.dto.notification.NotificationsPageDto;
import ru.team38.userservice.data.repositories.NotificationRepository;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final AccountService accountService;
    private final NotificationRepository notificationRepository;

    @LoggingMethod
    public DataTimestampDto getNotificationsCount() {
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        Integer count = notificationRepository.getNotificationsCountByAccountId(accountDto.getId());
        return new DataTimestampDto(ZonedDateTime.now(), new CountDto(count));
    }

    @LoggingMethod
    public NotificationsPageDto getNotificationsPage(Integer size) {
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        Integer count = notificationRepository.getNotificationsCountByAccountId(accountDto.getId());
        List<DataTimestampDto> notifications = notificationRepository.getNotificationsByAccountId(accountDto.getId(), size);
        return makePageDto(notifications, count, size);
    }

    public void readAllNotifications(Integer size) {
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        notificationRepository.updateNotificationsReadByAccountId(accountDto.getId(), size);
    }

    private NotificationsPageDto makePageDto(List<DataTimestampDto> notifications, Integer count, Integer size) {
        int number= 0, offset = 0;
        SortDto sortDto = new SortDto(false, true, notifications.isEmpty());
        return NotificationsPageDto.builder()
                .content(notifications)
                .totalElements(count)
                .totalPages((int) Math.ceil(count / (float) size))
                .number(number)
                .numberOfElements(notifications.size())
                .size(size)
                .sort(sortDto)
                .first(true)
                .last(!(count > notifications.size()))
                .pageable(new PageableDto(sortDto, number, notifications.size(), offset, false, false))
                .empty(notifications.isEmpty())
                .build();
    }
}
