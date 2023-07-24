package ru.team38.common.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private long id;
    private UUID authorId;
    private String content;
    private NotificationTypeEnum notificationType;
    private ZonedDateTime sentTime;
}
