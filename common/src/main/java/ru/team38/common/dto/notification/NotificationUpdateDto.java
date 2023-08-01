package ru.team38.common.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUpdateDto {
    NotificationTypeEnum notificationType;
    Boolean enable;
}
