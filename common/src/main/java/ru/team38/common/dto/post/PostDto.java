package ru.team38.common.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.like.ReactionDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Данные поста")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    @Schema(description = "Идентификатор поста")
    private UUID id;
    @Schema(description = "Флаг удаления поста")
    private Boolean isDeleted;
    @Schema(description = "Время создания поста", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", type = "string")
    private ZonedDateTime time;
    @Schema(description = "Время изменения поста", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", type = "string")
    private ZonedDateTime timeChanged;
    @Schema(description = "Идентификатор автора поста")
    private UUID authorId;
    @Schema(description = "Заголовок поста")
    private String title;
    @Schema(description = "Тип поста")
    private PostType type;
    @Schema(description = "Текст поста")
    private String postText;
    @Schema(description = "Флаг блокировки поста")
    private Boolean isBlocked;
    @Schema(description = "Количество комментариев")
    private Integer commentsCount;
    @Schema(description = "Реакции на пост")
    private List<ReactionDto> reactions;
    @Schema(description = "Моя реакция на пост")
    private String myReaction;
    @Schema(description = "Теги поста")
    private List<TagDto> tags;
    @Schema(description = "Количество лайков")
    private Integer likeAmount;
    @Schema(description = "Мой лайк")
    private Boolean myLike;
    @Schema(description = "Путь к изображению")
    private String imagePath;
    @Schema(description = "Дата публикации поста", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", type = "string")
    private ZonedDateTime publishDate;
}
