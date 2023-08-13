package ru.team38.common.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Уведомление")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    @Schema(description = "ID")
    private long id;

    @Schema(description = "ID автора")
    private UUID authorId;

    @Schema(description = "ID получателя")
    private UUID receiverId;

    @Schema(description = "Тип уведомления")
    private NotificationTypeEnum notificationType;

    @Schema(description = "Время отправки")
    private ZonedDateTime sendTime;

    @Schema(description = "Содержание")
    private String content;

    @Schema(description = "Прочитано ли")
    private boolean isReaded;
}
