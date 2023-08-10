package ru.team38.common.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.PublicationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLikeDto {
    private String reactionType;
    private PublicationType type;
}
