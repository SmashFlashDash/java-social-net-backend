package ru.team38.common.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.SortField;

import java.util.UUID;

@Schema(description = "Условия для поста")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConditionPostDto {

    @Schema(description = "Поле сортировки")
    private SortField<?> sort;

    @Schema(description = "Условия поиска")
    private Condition searchConditions;

    @Schema(description = "Идентификаторы аккаунтов")
    private UUID accountIds;

    @Schema(description = "Флаг \"с друзьями\"")
    private Boolean withFriends;
}
