package ru.team38.common.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.PublicationType;

@Schema(description = "Данные для создания лайка")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLikeDto {

    @Schema(description = "Тип реакции")
    private String reactionType;

    @Schema(description = "Тип публикации")
    private PublicationType type;
}
