package ru.team38.communicationsservice.data.repositories;

import lombok.RequiredArgsConstructor;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.post.*;
import ru.team38.common.jooq.Tables;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Friends;
import ru.team38.common.jooq.tables.Post;
import ru.team38.common.jooq.tables.Tag;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.PostRecord;
import ru.team38.common.jooq.tables.records.TagRecord;
import ru.team38.common.mappers.PostMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.select;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final DSLContext dsl;
    private final Post post = Post.POST;
    private final Friends friends = Friends.FRIENDS;
    private final Account account = Account.ACCOUNT;
    private final Tag tag = Tag.TAG;
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public List<PostDto> getPostDtosByEmail(ConditionPostDto conditionPostDto, String email) {
        UUID accountId = getUserIdByEmail(email);
        updateType();
        if (conditionPostDto.getAccountIds() != null) {
            return postMapper.postRecords2PostDtos(searchPostsByUserId(conditionPostDto, conditionPostDto.getAccountIds()));
        }
        if (!conditionPostDto.getWithFriends()) {
            return postMapper.postRecords2PostDtos(searchPostsByTag(conditionPostDto));
        }
        List<PostDto> list = postMapper.postRecords2PostDtos(searchPostsWithFriend(conditionPostDto, accountId));
        list.addAll(postMapper.postRecords2PostDtos(searchPostsByUserId(conditionPostDto, accountId)));
        return list;
    }

    public PostDto createPost(InsertPostDto insertPostDto, String email) {
        UUID accountId = getUserIdByEmail(email);
        addTags(insertPostDto.getTags());
        PostRecord postRecord = postMapper.InsertPostDto2PostRecord(insertPostDto, accountId);
        dsl.executeInsert(postRecord);
        return postMapper.postRecord2PostDto(postRecord);
    }

    public PostDto updatePost(InsertPostDto insertPostDto, UUID postId) {
        addTags(insertPostDto.getTags());
        return postMapper.postRecord2PostDto(dsl.update(post)
                .set(post.POST_TEXT, insertPostDto.getPostText())
                .set(post.IMAGE_PATH, insertPostDto.getImagePath())
                .set(post.TAGS, insertPostDto.getTags())
                .set(post.TIME_CHANGED, LocalDateTime.now())
                .set(post.TITLE, insertPostDto.getTitle())
                .where(post.ID.eq(postId))
                .returning()
                .fetchOne());
    }

    public PostDto getPostDtoById(UUID id) {
        Record postById = dsl.select()
                .from(post)
                .where(post.ID.eq(id))
                .fetchOne();
        return postMapper.postRecord2PostDto((PostRecord) postById);
    }

    public void deletePostById(UUID id) {
        deleteLikeComment(id);
        deleteComment(id);
        deleteLikePost(id);
        dsl.update(post)
                .set(post.IS_DELETED, true)
                .where(post.ID.eq(id)).execute();
    }

    public List<TagDto> getTags(String tagName) {
        List<TagRecord> tagRecord = dsl.selectFrom(tag)
                .where(tag.NAME.likeIgnoreCase(tagName + "%"))
                .fetch();
        if (!tagRecord.isEmpty()) {
            return postMapper.tagRecordToTagDto(tagRecord);
        } else {
            return Collections.emptyList();
        }
    }

    private List<PostRecord> searchPostsByUserId(ConditionPostDto conditionPostDto, UUID accountId) {
        return dsl.select()
                .from(post)
                .where(post.AUTHOR_ID.eq(accountId))
                .and(conditionPostDto.getSearchConditions())
                .orderBy(conditionPostDto.getSort())
                .fetch()
                .into(PostRecord.class);
    }

    private List<PostRecord> searchPostsWithFriend(ConditionPostDto conditionPostDto, UUID accountId) {
        return dsl.select()
                .from(post)
                .join(friends)
                .on(friends.REQUESTED_ACCOUNT_ID.eq(post.AUTHOR_ID))
                .where(friends.ACCOUNT_FROM_ID.eq(accountId))
                .and(friends.STATUS_CODE.eq("FRIEND"))
                .and(conditionPostDto.getSearchConditions())
                .orderBy(conditionPostDto.getSort())
                .fetch()
                .into(PostRecord.class);
    }
    private List<PostRecord> searchPostsByTag (ConditionPostDto conditionPostDto){
        return dsl.select()
                .from(post)
                .where(conditionPostDto.getSearchConditions())
                .orderBy(conditionPostDto.getSort())
                .fetch()
                .into(PostRecord.class);
    }


    private void updateType(){
        dsl.update(post)
                .set(post.TYPE, PostType.POSTED.toString())
                .where(post.PUBLISH_DATE.lessThan(LocalDateTime.now()))
                .and(post.TYPE.eq(PostType.QUEUED.toString()))
                .execute();
    }

    private void addTags(String[] tags){
        for (String tagName : tags) {
            if (!tagExists(tagName)) {
                dsl.executeInsert(postMapper.tagNameToTagRecord(tagName));
            }
        }
    }
    private void deleteLikePost(UUID id){
        dsl.update(Tables.LIKE)
                .set(Tables.LIKE.IS_DELETED, true)
                .where(Tables.LIKE.ITEM_ID.eq(id)).execute();
    }
    private void deleteComment(UUID id){
        dsl.update(Tables.COMMENT)
                .set(Tables.COMMENT.IS_DELETED, true)
                .where(Tables.COMMENT.POST_ID.eq(id)).execute();
    }
    private void deleteLikeComment(UUID id) {
        dsl.update(Tables.LIKE)
                .set(Tables.LIKE.IS_DELETED, true)
                .where(Tables.LIKE.ITEM_ID.in(
                        select(Tables.COMMENT.ID)
                                .from(Tables.COMMENT)
                                .where(Tables.COMMENT.POST_ID.eq(id))
                ))
                .execute();
    }

    private Boolean tagExists(String tagName){
        return dsl.fetchExists(DSL
                .selectFrom(tag)
                .where(tag.NAME.eq(tagName)));
    }

    private UUID getUserIdByEmail(String email){
        AccountRecord accountRecord = dsl.selectFrom(account)
                .where(account.EMAIL.eq(email))
                .fetchOne();
        return accountRecord == null ? null : accountRecord.getId();
    }
}
