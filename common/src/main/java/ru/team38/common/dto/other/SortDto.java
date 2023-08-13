package ru.team38.common.dto.other;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Данные сортировки")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortDto {

    @Schema(description = "Не отсортировано")
    private boolean unsorted;

    @Schema(description = "Отсортировано")
    private boolean sorted;

    @Schema(description = "Пусто")
    private boolean empty;
}