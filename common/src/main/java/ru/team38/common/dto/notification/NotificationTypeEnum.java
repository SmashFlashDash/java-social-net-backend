package ru.team38.common.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип уведомления")
public enum NotificationTypeEnum {

    @Schema(description = "Лайк")
    LIKE,

    @Schema(description = "Публикация")
    POST,

    @Schema(description = "Комментарий к публикации")
    POST_COMMENT,

    @Schema(description = "Комментарий к комментарию")
    COMMENT_COMMENT,

    @Schema(description = "Сообщение")
    MESSAGE,

    @Schema(description = "Запрос в друзья")
    FRIEND_REQUEST,

    @Schema(description = "День рождения друга")
    FRIEND_BIRTHDAY,

    @Schema(description = "Отправка уведомления по электронной почте")
    SEND_EMAIL_MESSAGE
}