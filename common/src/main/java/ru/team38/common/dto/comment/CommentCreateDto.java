package ru.team38.common.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Данные для создания комментария")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {
    @Schema(description = "Текст комментария")
    private String commentText;
}
