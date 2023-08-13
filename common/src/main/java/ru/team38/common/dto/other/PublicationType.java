package ru.team38.common.dto.other;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип публикации")
public enum PublicationType {

    @Schema(description = "Комментарий")
    COMMENT,

    @Schema(description = "Пост")
    POST
}
