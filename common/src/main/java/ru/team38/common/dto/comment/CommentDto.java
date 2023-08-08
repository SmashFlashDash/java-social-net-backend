package ru.team38.common.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.PublicationType;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Данные комментария")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    @Schema(description = "ID комментария")
    private UUID id;

    @Schema(description = "Удален ли комментарий")
    private Boolean isDeleted;

    @Schema(description = "Тип публикации", example = "POST")
    private PublicationType commentType;

    @Schema(description = "Время комментария")
    private ZonedDateTime time;

    @Schema(description = "Время изменения комментария")
    private ZonedDateTime timeChanged;

    @Schema(description = "ID автора")
    private UUID authorId;

    @Schema(description = "ID родительского комментария")
    private UUID parentId;

    @Schema(description = "Текст комментария")
    private String commentText;

    @Schema(description = "ID поста")
    private UUID postId;

    @Schema(description = "Заблокирован ли комментарий")
    private Boolean isBlocked;

    @Schema(description = "Количество лайков")
    private Integer likeAmount;

    @Schema(description = "Флаг авторства лайка")
    private Boolean myLike;

    @Schema(description = "Количество комментариев")
    private Integer commentsCount;

    @Schema(description = "Путь к изображению")
    private String imagePath;
}
