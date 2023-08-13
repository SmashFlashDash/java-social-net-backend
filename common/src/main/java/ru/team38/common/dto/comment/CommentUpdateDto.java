package ru.team38.common.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "Данные для обновления комментария")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateDto {
    @Schema(description = "Текст комментария")
    private String commentText;
    @Schema(description = "ID комментария")
    private UUID id;
}
