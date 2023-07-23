package ru.team38.communicationsservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.jooq.Tables;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.CommentRecord;
import ru.team38.common.mappers.CommentMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final DSLContext dsl;
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    public CommentDto createComment(CommentDto commentDto) {
        CommentRecord commentRecord = dsl.insertInto(Tables.COMMENT)
                .set(commentMapper.commentDtoToCommentRecord(commentDto))
                .returning()
                .fetchOne();
        return commentMapper.commentRecordToCommentDto(commentRecord);
    }

    public CommentDto updateComment(UUID id, String newText) {
        CommentRecord commentRecord = dsl.update(Tables.COMMENT)
                .set(Tables.COMMENT.COMMENT_TEXT, newText)
                .set(Tables.COMMENT.TIME_CHANGED, OffsetDateTime.now())
                .where(Tables.COMMENT.ID.eq(id))
                .returning()
                .fetchOne();
        return commentMapper.commentRecordToCommentDto(commentRecord);
    }

    public void deleteComment(UUID commentId) {
        dsl.deleteFrom(Tables.COMMENT)
                .where(Tables.COMMENT.ID.eq(commentId))
                .execute();
    }

    public List<CommentDto> getMainComments(Long postId, Pageable pageable) {
        return getComments(Tables.COMMENT.POST_ID.eq(postId).and(Tables.COMMENT.PARENT_ID.isNull()), pageable);
    }

    public List<CommentDto> getSubComments(UUID commentId, Pageable pageable) {
        return getComments(Tables.COMMENT.PARENT_ID.eq(commentId), pageable);
    }

    private List<CommentDto> getComments(Condition condition, Pageable pageable) {
        SortField<?>[] sortFields = pageable.getSort().stream()
                .map(order -> {
                    Field<Object> field = DSL.field(order.getProperty());
                    return order.isAscending() ? field.asc() : field.desc();
                })
                .toArray(SortField<?>[]::new);

        return dsl.selectFrom(Tables.COMMENT)
                .where(condition)
                .and(Tables.COMMENT.IS_DELETED.eq(false))
                .orderBy(sortFields)
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch()
                .map(commentMapper::commentRecordToCommentDto);
    }

    public UUID getIdByUsername(String username) {
        AccountRecord rec = dsl.selectFrom(Account.ACCOUNT)
                .where(Account.ACCOUNT.EMAIL.eq(username))
                .fetchOne();
        return rec == null ? null : rec.get(Account.ACCOUNT.ID);
    }

    public String getUsernameByCommentId(UUID id) {
        AccountRecord rec = dsl.select(Tables.ACCOUNT.fields())
                .from(Tables.ACCOUNT)
                .join(Tables.COMMENT)
                .on(Tables.ACCOUNT.ID.eq(Tables.COMMENT.AUTHOR_ID))
                .where(Tables.COMMENT.ID.eq(id))
                .fetchOneInto(AccountRecord.class);
        return rec == null ? null : rec.get(Tables.ACCOUNT.EMAIL);
    }
}
