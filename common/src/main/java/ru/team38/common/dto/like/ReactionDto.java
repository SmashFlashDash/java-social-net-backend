package ru.team38.common.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionDto {
    private String reactionType;
    private Integer count;
}
