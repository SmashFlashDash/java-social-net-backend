package ru.team38.common.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "DTO для вставки поста")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InsertPostDto {

    @Schema(description = "Путь к изображению")
    private String imagePath;

    @Schema(description = "Текст поста")
    private String postText;

    @Schema(description = "Заголовок")
    private String title;

    @Schema(description = "Время")
    private LocalDateTime time;

    @Schema(description = "Тип")
    private String type;

    @Schema(description = "Дата публикации")
    private LocalDateTime publishDate;

    @Schema(description = "Теги")
    private String[] tags;
}
