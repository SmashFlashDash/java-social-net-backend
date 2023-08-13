package ru.team38.common.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Объект тега")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {

    @Schema(description = "ID тега")
    private Long id;

    @Schema(description = "Флаг удаления")
    private Boolean isDeleted;

    @Schema(description = "Имя тега")
    private String name;
}
