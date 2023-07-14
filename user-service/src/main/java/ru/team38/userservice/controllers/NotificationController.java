package ru.team38.userservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.team38.common.dto.notification.NotificationCountDto;
import ru.team38.userservice.services.NotificationService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/count")
    public ResponseEntity<NotificationCountDto> getNotificationsCount() {
        return ResponseEntity.ok(notificationService.getNotificationsCount());
    }
}
