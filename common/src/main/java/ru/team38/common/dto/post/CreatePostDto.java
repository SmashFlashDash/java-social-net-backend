package ru.team38.common.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(description = "DTO для создания поста")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostDto {

    @Schema(description = "Идентификатор поста")
    private UUID id;

    @Schema(description = "Путь к изображению")
    private String imagePath;

    @Schema(description = "Текст поста")
    private String postText;

    @Schema(description = "Дата публикации")
    private String publishDate;

    @Schema(description = "Теги")
    private List<TagDto> tags;

    @Schema(description = "Заголовок")
    private String title;
}
