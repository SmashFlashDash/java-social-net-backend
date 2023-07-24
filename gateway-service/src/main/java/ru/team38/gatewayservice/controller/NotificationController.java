package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.notification.NotificationsPageDto;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.gatewayservice.service.UserService;

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
        return ResponseEntity.ok(userService.readAllNotifiicatons());
    }


    @GetMapping("/count")
    public DataTimestampDto getNotificationsCount() {
        return userService.getNotificationsCount();
    }
}
