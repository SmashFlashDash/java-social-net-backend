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
    public LikeDto getLikeByReactionTypeAndEmail(String reactionType, String email, UUID itemId){
        UUID authorId = emailUser2idUser(email);
        PublicationType likeType;
        if (reactionType != null) {
            likeType = PublicationType.POST;
        } else {
            likeType = PublicationType.COMMENT;
        }
        LikeRecord likeRecord = likeMapper.map2LikeRecord(reactionType, likeType.toString(), authorId, itemId);

        if (isMyLike(authorId, itemId)) {
            deleteLike(itemId, email);
        }
        dsl.executeInsert(likeRecord);

        if (likeType == PublicationType.POST) {
            updatePostByLike(itemId, isAuthorOfPost(authorId, itemId), reactionType);
        } else {
            updateCommentByLike(itemId, authorId, isAuthorOfComment(authorId, itemId));
        }
        return likeMapper.LikeRecord2likeDto(likeRecord);
    }
    public void deleteLike(UUID itemId, String email){
        UUID accountId = emailUser2idUser(email);
        Boolean isPost = isPost(itemId);
        Boolean isMyPost = isAuthorOfPost(accountId, itemId);
        Boolean isMyComment = isAuthorOfComment(accountId, itemId);
        dsl.update(like)
                .set(like.IS_DELETED, true)
                .where(like.ITEM_ID.eq(itemId))
                .and(like.AUTHOR_ID.eq(accountId))
                .execute();
        if (isPost) {
            updatePostByLike(itemId, isMyPost);
        }else {
            updateCommentByLike(itemId, accountId, isMyComment);
        }
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
        for (LikeDto listLike : likeMapper.map2LikeDtoList(likeRecords)) {
            boolean found = false;
            for (ReactionDto reactionDto : reactions) {
                if (reactionDto.getReactionType().equals(listLike.getReactionType())) {
                    reactionDto.setCount(reactionDto.getCount() + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                reactions.add(new ReactionDto(listLike.getReactionType(), 1));
            }
        }
        return reactions;
    }

    private void updatePostByLike(UUID itemId, Boolean isMyPost){
        updatePostByLike(itemId, isMyPost, null);
    }

    private void updatePostByLike(UUID itemId, Boolean isMyPost, String reaction) {
        List<LikeRecord> likeRecords = getLikeRecords(itemId, PublicationType.POST.toString());
        List<ReactionDto> reactions = getReactions(likeRecords);
        int likeAmount = likeRecords.size();
        UpdateSetMoreStep<PostRecord> updateQuery = dsl.update(post)
                .set(post.LIKE_AMOUNT, likeAmount)
                .set(post.REACTIONS, listObject2ListArray(reactions));

        Condition condition = post.ID.eq(itemId);
        if (isMyPost) {
            Boolean myLike = reaction != null;
            updateQuery = updateQuery
                    .set(post.MY_LIKE, myLike)
                    .set(post.MY_REACTION, reaction);
        }
        updateQuery.where(condition).returning().fetchOne();
    }


    private void updateCommentByLike(UUID itemId, UUID author, Boolean isMyComment){
        int likeAmount = getLikeRecords(itemId, PublicationType.COMMENT.toString()).size();
        UpdateSetMoreStep<CommentRecord> updateQuery = dsl.update(comment)
                .set(comment.LIKE_AMOUNT, likeAmount);
        Condition condition = comment.ID.eq(itemId);
        if (isMyComment) {
            Boolean myLike = isAuthorOfComment(author, itemId);
            updateQuery = updateQuery
                    .set(comment.MY_LIKE, myLike);
        }
        updateQuery.where(condition).returning().fetchOne();
    }

    private Boolean isAuthorOfPost(UUID author, UUID itemId) {
        return dsl.fetchExists(DSL.select()
                .from(post)
                .join(like)
                .on(post.ID.eq(itemId))
                .where(post.AUTHOR_ID.eq(author)));
    }
    private Boolean isAuthorOfComment(UUID author, UUID itemId) {
        return dsl.fetchExists(DSL.select()
                .from(comment)
                .join(like)
                .on(comment.ID.eq(itemId))
                .where(comment.AUTHOR_ID.eq(author)));
    }

    private Boolean isPost(UUID itemId){
        return dsl.fetchExists(DSL.selectFrom(like)
                .where(like.ITEM_ID.eq(itemId))
                .and(like.TYPE.eq(PublicationType.POST.toString())));
    }

    private Boolean isMyLike(UUID authorId, UUID itemId){
        return dsl.fetchExists(DSL.selectFrom(like)
                .where(like.ITEM_ID.eq(itemId))
                .and(like.AUTHOR_ID.eq(authorId))
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

    private UUID emailUser2idUser(String email){
        AccountRecord accountRecord = dsl.selectFrom(account)
                .where(account.EMAIL.eq(email))
                .fetchOne();
        return accountRecord == null ? null : accountRecord.getId();
    }
}
