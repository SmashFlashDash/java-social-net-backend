package ru.team38.common.dto.other;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Ответ с данными страницы")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    @Schema(description = "Общее количество элементов")
    private Integer totalElements;
    @Schema(description = "Общее количество страниц")
    private Integer totalPages;
    @Schema(description = "Номер страницы")
    private Integer number;
    @Schema(description = "Размер страницы")
    private Integer size;
    @Schema(description = "Первая страница")
    private Boolean first;
    @Schema(description = "Последняя страница")
    private Boolean last;
    @Schema(description = "Количество элементов на странице")
    private Integer numberOfElements;
    @Schema(description = "Пустая страница")
    private Boolean empty;
    @Schema(description = "Данные сортировки")
    private SortDto sort;
    @Schema(description = "Данные для постраничного вывода")
    private PageableDto pageable;
    @ArraySchema(
            arraySchema = @Schema(description = "Содержимое страницы"),
            uniqueItems = true,
            minItems = 0
    )
    private List<T> content;

    public void addToContent(T item) {
        if (content == null) {
            content = new ArrayList<>();
        }
        content.add(item);
    }

    public List<T> getContent() {
        return content == null ? new ArrayList<>() : content;
    }
}
