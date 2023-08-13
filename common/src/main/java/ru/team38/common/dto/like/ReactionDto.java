package ru.team38.common.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Данные реакции")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionDto {

    @Schema(description = "Тип реакции")
    private String reactionType;

    @Schema(description = "Количество")
    private Integer count;
}