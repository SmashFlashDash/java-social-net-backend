package ru.team38.common.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddNotificationDto {
    private UUID authorId;
    private UUID receiverId;
    private NotificationTypeEnum notificationType;
    private String content;
}
