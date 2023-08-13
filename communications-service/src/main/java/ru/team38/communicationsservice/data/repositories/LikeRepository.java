package ru.team38.communicationsservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.common.dto.other.PublicationType;
import ru.team38.common.dto.like.ReactionDto;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Comment;
import ru.team38.common.jooq.tables.Like;
import ru.team38.common.jooq.tables.Post;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.CommentRecord;
import ru.team38.common.jooq.tables.records.LikeRecord;
import ru.team38.common.jooq.tables.records.PostRecord;
import ru.team38.common.mappers.LikeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class LikeRepository {
    private final DSLContext dsl;
    private static final Post post = Post.POST;
    private static final Account account = Account.ACCOUNT;
    private static final Like like = Like.LIKE;
    private static final Comment comment = Comment.COMMENT;
    private final LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);
    public UUID emailUser2idUser(String email){
        AccountRecord accountRecord = dsl.selectFrom(account)
                .where(account.EMAIL.eq(email))
                .fetchOne();
        return accountRecord == null ? null : accountRecord.getId();
    }
    public LikeDto getLikeByComment(LikeDto likeDto){
        LikeRecord likeRecord = likeMapper.map2LikeRecord(likeDto);
        dsl.executeInsert(likeRecord);

        updateCommentByLike(likeDto, isAuthorOfComment(likeDto));

        return likeMapper.LikeRecord2likeDto(likeRecord);
    }
    public LikeDto getLikeByPost(LikeDto likeDto){
        if (isMyLike(likeDto)) {
            deleteLike(likeDto);
        }

        LikeRecord likeRecord = likeMapper.map2LikeRecord(likeDto);
        dsl.executeInsert(likeRecord);

        updatePostByLike(likeDto, isAuthorOfPost(likeDto));

        return likeMapper.LikeRecord2likeDto(likeRecord);
    }
    public void deleteLike(LikeDto likeDto){
        UUID itemId = likeDto.getItemId();
        Boolean isPost = isPost(itemId);
        Boolean isMyPost = isAuthorOfPost(likeDto);
        Boolean isMyComment = isAuthorOfComment(likeDto);
        dsl.update(like)
                .set(like.IS_DELETED, true)
                .where(like.ITEM_ID.eq(itemId))
                .and(like.AUTHOR_ID.eq(likeDto.getAuthorId()))
                .execute();
        if (isPost) {
            updatePostByLike(likeDto, isMyPost);
        }else {
            updateCommentByLike(likeDto, isMyComment);
        }
    }

    private void updatePostByLike(LikeDto likeDto, Boolean isMyPost) {
        List<LikeRecord> likeRecords = getLikeRecords(likeDto.getItemId(), PublicationType.POST.toString());
        List<ReactionDto> reactions = getReactions(likeRecords);
        int likeAmount = likeRecords.size();
        UpdateSetMoreStep<PostRecord> updateQuery = dsl.update(post)
                .set(post.LIKE_AMOUNT, likeAmount)
                .set(post.REACTIONS, listObject2ListArray(reactions));

        Condition condition = post.ID.eq(likeDto.getItemId());
        if (isMyPost) {
            Boolean myLike = likeDto.getReactionType() != null;
            updateQuery = updateQuery
                    .set(post.MY_LIKE, myLike)
                    .set(post.MY_REACTION, likeDto.getReactionType());
        }
        updateQuery.where(condition).returning().fetchOne();
    }


    private void updateCommentByLike(LikeDto likeDto, Boolean isMyComment){
        int likeAmount = getLikeRecords(likeDto.getItemId(), PublicationType.COMMENT.toString()).size();
        UpdateSetMoreStep<CommentRecord> updateQuery = dsl.update(comment)
                .set(comment.LIKE_AMOUNT, likeAmount);
        Condition condition = comment.ID.eq(likeDto.getItemId());
        if (isMyComment) {
            Boolean myLike = isAuthorOfComment(likeDto);
            updateQuery = updateQuery
                    .set(comment.MY_LIKE, myLike);
        }
        updateQuery.where(condition).returning().fetchOne();
    }

    private List<LikeRecord> getLikeRecords(UUID itemId, String type){
        return dsl.selectFrom(like)
                .where(like.ITEM_ID.eq(itemId))
                .and(like.TYPE.eq(type))
                .and((like.IS_DELETED.eq(false)))
                .fetch();
    }

    private List<ReactionDto> getReactions(List<LikeRecord> likeRecords){
        List<ReactionDto> reactions = new ArrayList<>();
        List<LikeDto> likeDtoList = likeRecords.stream().map(likeMapper::LikeRecord2likeDto).toList();
        for (LikeDto likeDto : likeDtoList) {
            boolean found = false;
            for (ReactionDto reactionDto : reactions) {
                if (reactionDto.getReactionType().equals(likeDto.getReactionType())) {
                    reactionDto.setCount(reactionDto.getCount() + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                reactions.add(new ReactionDto(likeDto.getReactionType(), 1));
            }
        }
        return reactions;
    }


    private Boolean isAuthorOfPost(LikeDto likeDto) {
        return dsl.fetchExists(DSL.select()
                .from(post)
                .join(like)
                .on(post.ID.eq(likeDto.getItemId()))
                .where(post.AUTHOR_ID.eq(likeDto.getAuthorId())));
    }
    private Boolean isAuthorOfComment(LikeDto likeDto) {
        return dsl.fetchExists(DSL.select()
                .from(comment)
                .join(like)
                .on(comment.ID.eq(likeDto.getItemId()))
                .where(comment.AUTHOR_ID.eq(likeDto.getAuthorId())));
    }

    private Boolean isPost(UUID itemId){
        return dsl.fetchExists(DSL.selectFrom(like)
                .where(like.ITEM_ID.eq(itemId))
                .and(like.TYPE.eq(PublicationType.POST.toString())));
    }

    private Boolean isMyLike(LikeDto likeDto){
        return dsl.fetchExists(DSL.selectFrom(like)
                .where(like.ITEM_ID.eq(likeDto.getItemId()))
                .and(like.AUTHOR_ID.eq(likeDto.getAuthorId()))
                .and(like.IS_DELETED.eq(false)));
    }

    private String[] listObject2ListArray(List<ReactionDto> reactions){
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (ReactionDto reactionDto : reactions) {
            String reactionType = reactionDto.getReactionType();
            int count = reactionDto.getCount();
            stringJoiner.add(reactionType + " " + count);
        }
        return stringJoiner.toString().split(", ");
    }

}
