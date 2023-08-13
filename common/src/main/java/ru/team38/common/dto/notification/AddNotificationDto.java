package ru.team38.common.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "Добавление уведомления")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddNotificationDto {

    @Schema(description = "ID автора")
    private UUID authorId;

    @Schema(description = "ID получателя")
    private UUID receiverId;

    @Schema(description = "Тип уведомления")
    private NotificationTypeEnum notificationType;

    @Schema(description = "Содержание")
    private String content;
}
