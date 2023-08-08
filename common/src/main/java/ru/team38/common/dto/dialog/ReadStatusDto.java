package ru.team38.common.dto.dialog;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус сообщения")
public enum ReadStatusDto {
    @Schema(description = "Отправлено")
    SENT,
    @Schema(description = "Прочитано")
    READ
}
