package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.dto.notification.NotificationUpdateDto;
import ru.team38.common.dto.notification.NotificationsPageDto;
import ru.team38.gatewayservice.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<NotificationsPageDto> getNotifications() {
        return ResponseEntity.ok(userService.getNotificationsPage());
    }

    @PutMapping("/readed")
    public ResponseEntity<String> readAllNotifications() {
        return ResponseEntity.ok(userService.readAllNotifications());
    }

    @GetMapping("/count")
    public DataTimestampDto getNotificationsCount() {
        return userService.getNotificationsCount();
    }

    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingDto> getNotificationSetting() {
        return ResponseEntity.ok(userService.getNotificationSetting());
    }

    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingDto> updateNotificationSetting(@RequestBody NotificationUpdateDto notificationUpdateDto) {
        return ResponseEntity.ok(userService.updateNotificationSetting(notificationUpdateDto));
    }

    @PostMapping("/settings/{id}")
    public ResponseEntity<NotificationSettingDto> setNotificationSetting(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.setNotificationSetting(id));
    }
}
