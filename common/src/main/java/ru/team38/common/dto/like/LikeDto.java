package ru.team38.common.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.PublicationType;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Данные лайка")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeDto {

    @Schema(description = "ID лайка")
    private UUID id;

    @Schema(description = "ID автора")
    private UUID authorId;

    @Schema(description = "Удален ли лайк")
    private Boolean isDeleted;

    @Schema(description = "ID объекта лайка")
    private UUID itemId;

    @Schema(description = "Тип реакции")
    private String reactionType;

    @Schema(description = "Время создания")
    private ZonedDateTime time;

    @Schema(description = "Тип публикации")
    private PublicationType type;
}