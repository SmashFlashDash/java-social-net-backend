package ru.team38.common.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Форма для ответа по уведомлениям")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    @Schema(description = "ID")
    private long id;

    @Schema(description = "ID автора")
    private UUID authorId;

    @Schema(description = "Содержание")
    private String content;

    @Schema(description = "Тип уведомления")
    private NotificationTypeEnum notificationType;

    @Schema(description = "Время отправки")
    private ZonedDateTime sentTime;
}
