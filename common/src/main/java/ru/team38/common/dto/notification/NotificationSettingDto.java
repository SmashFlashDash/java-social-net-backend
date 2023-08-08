package ru.team38.common.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "Настройки уведомлений")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingDto {

    @Schema(description = "ID настройки")
    UUID id;

    @Schema(description = "Уведомления о публикациях")
    Boolean enablePost;

    @Schema(description = "Уведомления о комментариях к публикациям")
    Boolean enablePostComment;

    @Schema(description = "Уведомления о комментариях к комментариям")
    Boolean enableCommentComment;

    @Schema(description = "Уведомления о сообщениях")
    Boolean enableMessage;

    @Schema(description = "Уведомления о запросах в друзья")
    Boolean enableFriendRequest;

    @Schema(description = "Уведомления о днях рождения друзей")
    Boolean enableFriendBirthday;

    @Schema(description = "Отправка уведомлений по электронной почте")
    Boolean enableSendEmailMessage;

    @Schema(description = "Удалена ли")
    Boolean isDeleted;
}
