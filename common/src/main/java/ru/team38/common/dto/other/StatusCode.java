package ru.team38.common.dto.other;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Код статуса")
public enum StatusCode {

    @Schema(description = "Друг")
    FRIEND,

    @Schema(description = "Запрос в друзья к")
    REQUEST_TO,

    @Schema(description = "Запрос в друзья от")
    REQUEST_FROM,

    @Schema(description = "Заблокирован")
    BLOCKED,

    @Schema(description = "Отклонено")
    DECLINED,

    @Schema(description = "Подписан")
    SUBSCRIBED,

    @Schema(description = "Нет")
    NONE,

    @Schema(description = "Наблюдение / активность без подписки")
    WATCHING,

    @Schema(description = "Отклонение")
    REJECTING,

    @Schema(description = "Рекомендация")
    RECOMMENDATION
}