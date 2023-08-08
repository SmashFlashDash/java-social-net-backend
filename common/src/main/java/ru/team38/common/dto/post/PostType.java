package ru.team38.common.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;

public enum PostType {
    @Schema(description = "Опубликовано")
    POSTED,
    @Schema(description = "В очереди")
    QUEUED
}
