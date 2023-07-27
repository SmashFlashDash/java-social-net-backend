package ru.team38.userservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.team38.common.dto.notification.NotificationsPageDto;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.userservice.services.NotificationService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final Integer size = 20;

    @GetMapping
    public ResponseEntity<NotificationsPageDto> getNotificationsPage() {
        return ResponseEntity.ok(notificationService.getNotificationsPage(size));
    }

    @PutMapping("/readed")
    public ResponseEntity<String> readAllNotifications() {
        notificationService.readAllNotifications(size);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/count")
    public ResponseEntity<DataTimestampDto> getNotificationsCount() {
        return ResponseEntity.ok(notificationService.getNotificationsCount());
    }
}
