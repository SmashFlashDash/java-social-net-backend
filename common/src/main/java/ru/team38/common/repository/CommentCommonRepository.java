package ru.team38.common.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Comment;
import ru.team38.common.jooq.tables.records.CommentRecord;
import ru.team38.common.mappers.AccountMapper;
import ru.team38.common.mappers.CommentMapper;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentCommonRepository {
    private final DSLContext DSL;
    private final Comment COMMENT = Comment.COMMENT;
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    public CommentDto getCommentById(UUID itemId) {
        CommentRecord comment = DSL.selectFrom(COMMENT).where(COMMENT.ID.eq(itemId)).fetchOne();
        return commentMapper.commentRecordToCommentDto(comment);
    }
}
