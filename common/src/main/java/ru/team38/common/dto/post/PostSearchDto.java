package ru.team38.common.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(description = "Объект для поиска постов")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchDto {

    @Schema(description = "Флаг \"с друзьями\"")
    private Boolean withFriends;

    @Schema(description = "Сортировка")
    private List<String> sort;

    @Schema(description = "Флаг удаления")
    private Boolean isDeleted;

    @Schema(description = "ID аккаунта")
    private UUID accountIds;

    @Schema(description = "Теги")
    private List<String> tags;

    @Schema(description = "Дата с")
    private String dateFrom;

    @Schema(description = "Дата по")
    private String dateTo;

    @Schema(description = "Автор")
    private String author;

    @Schema(description = "Текст")
    private String text;
}
