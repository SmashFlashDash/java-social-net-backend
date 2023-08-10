package ru.team38.common.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.PublicationType;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private UUID id;
    private Boolean isDeleted;
    private PublicationType commentType;
    private ZonedDateTime time;
    private ZonedDateTime timeChanged;
    private UUID authorId;
    private UUID parentId;
    private String commentText;
    private UUID postId;
    private Boolean isBlocked;
    private Integer likeAmount;
    private Boolean myLike;
    private Integer commentsCount;
    private String imagePath;
}
