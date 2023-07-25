package ru.team38.common.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    private UUID id;
    private UUID authorId;
    private Boolean isDeleted;
    private UUID itemId;
    private String reactionType;
    private ZonedDateTime time;
    private LikeType type;
}
