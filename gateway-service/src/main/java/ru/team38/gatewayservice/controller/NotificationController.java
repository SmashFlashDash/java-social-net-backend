package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.dto.notification.NotificationUpdateDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.gatewayservice.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerInterface {

    private final UserService userService;

    @Override
    @GetMapping
    public ResponseEntity<PageResponseDto<DataTimestampDto>> getNotifications(@RequestHeader("x-lang") String lang) {
        return ResponseEntity.ok(userService.getNotificationsPage(lang));
    }

    @Override
    @PutMapping("/readed")
    public ResponseEntity<String> readAllNotifications() {
        return ResponseEntity.ok(userService.readAllNotifications());
    }

    @Override
    @GetMapping("/count")
    public DataTimestampDto getNotificationsCount() {
        return userService.getNotificationsCount();
    }

    @Override
    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingDto> getNotificationSetting() {
        return ResponseEntity.ok(userService.getNotificationSetting());
    }

    @Override
    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingDto> updateNotificationSetting(@RequestBody NotificationUpdateDto notificationUpdateDto) {
        return ResponseEntity.ok(userService.updateNotificationSetting(notificationUpdateDto));
    }

    @Override
    @PostMapping("/settings/{id}")
    public ResponseEntity<NotificationSettingDto> setNotificationSetting(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.setNotificationSetting(id));
    }
}
