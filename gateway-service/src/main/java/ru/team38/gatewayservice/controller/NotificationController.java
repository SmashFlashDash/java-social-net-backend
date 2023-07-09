package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.team38.common.dto.notification.NotificationCountDto;
import ru.team38.gatewayservice.service.UserService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final UserService userService;

    @GetMapping("/count")
    public NotificationCountDto getNotificationsCount() {
        return userService.getNotificationsCount();
    }
}
