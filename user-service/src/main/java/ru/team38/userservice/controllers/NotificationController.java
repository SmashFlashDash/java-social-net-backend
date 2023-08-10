package ru.team38.userservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.dto.notification.NotificationUpdateDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.userservice.services.NotificationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private static final Integer SIZE = 20;

    @GetMapping
    public ResponseEntity<PageResponseDto<DataTimestampDto>> getNotificationsPage(@RequestHeader("x-lang") String lang) {
        return ResponseEntity.ok(notificationService.getNotificationsPage(lang, SIZE));
    }

    @PutMapping("/readed")
    public ResponseEntity<String> readAllNotifications() {
        notificationService.readAllNotifications(SIZE);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/count")
    public ResponseEntity<DataTimestampDto> getNotificationsCount() {
        return ResponseEntity.ok(notificationService.getNotificationsCount());
    }

    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingDto> getNotificationSetting(HttpServletRequest request) {
        return ResponseEntity.ok(notificationService.getNotificationSetting(request));
    }

    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingDto> updateNotificationSetting(HttpServletRequest request,
                                                                            @RequestBody NotificationUpdateDto notificationUpdateDto) {
        return ResponseEntity.ok(notificationService.updateNotificationSetting(request, notificationUpdateDto));
    }

    @PostMapping("/settings/{id}")
    public ResponseEntity<NotificationSettingDto> setNotificationSetting(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.setNotificationSetting(id));
    }
}
