package ru.team38.common.dto.other;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Данные для постраничного вывода")
@Data
@AllArgsConstructor
public class PageableDto {

    @Schema(description = "Сортировка")
    private SortDto sort;

    @Schema(description = "Номер страницы")
    private int pageNumber;

    @Schema(description = "Размер страницы")
    private int pageSize;

    @Schema(description = "Смещение")
    private int offset;

    @Schema(description = "Без разбивки на страницы")
    private boolean unpaged;

    @Schema(description = "Разбито на страницы")
    private boolean paged;
}
