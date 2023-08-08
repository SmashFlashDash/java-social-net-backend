package ru.team38.common.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Обновление уведомления")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUpdateDto {

    @Schema(description = "Тип уведомления")
    NotificationTypeEnum notificationType;

    @Schema(description = "Включено ли")
    Boolean enable;
}
