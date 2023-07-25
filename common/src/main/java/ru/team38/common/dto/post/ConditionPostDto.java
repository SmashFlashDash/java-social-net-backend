package ru.team38.common.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.SortField;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConditionPostDto {
    private SortField<?> sort;
    private Condition searchConditions;
    private UUID accountIds;
    private Boolean withFriends;
}
