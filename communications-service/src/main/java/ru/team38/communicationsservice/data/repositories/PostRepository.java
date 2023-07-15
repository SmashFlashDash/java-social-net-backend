package ru.team38.communicationsservice.data.repositories;

import lombok.RequiredArgsConstructor;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.post.*;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Friends;
import ru.team38.common.jooq.tables.Post;
import ru.team38.common.jooq.tables.Tag;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.PostRecord;
import ru.team38.common.jooq.tables.records.TagRecord;
import ru.team38.common.mappers.PostMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final DSLContext dsl;
    private final Post post = Post.POST;
    private final Friends friends = Friends.FRIENDS;
    private final Account account = Account.ACCOUNT;
    private final Tag tag = Tag.TAG;
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public List<PostDto> getPostDtosByEmail (PostSearchDto postSearchDto, String email){
        UUID accountId = getUserIdByEmail(email);
        updateType();
        if(postSearchDto.getIsDeleted()) {
            return null;
        }
        if(postSearchDto.getAccountIds() != null){
            return postMapper.postRecords2PostDtos(getPostRecords(postSearchDto));
        }
        if(postSearchDto.getDateTo() != null ){
            return postMapper.postRecords2PostDtos(searchPostsByUserId(postSearchDto, accountId));
        }
        return postMapper.postRecords2PostDtos(getAllPostRecords(postSearchDto, accountId));
    }

    public PostDto createPost(CreatePostDto createPostDto, String email) {
        UUID accountId = getUserIdByEmail(email);
        return postMapper.postRecord2PostDto(createPost(createPostDto, accountId));
    }

    public PostDto updatePost(CreatePostDto createPostDto) {
        return postMapper.postRecord2PostDto(updatePostRecord(createPostDto));
    }

    public PostDto getPostDtoById(Long id){
        Record postById = dsl.select()
                .from(post)
                .where(post.ID.eq(id))
                .fetchOne();
        return postMapper.postRecord2PostDto((PostRecord) postById);
    }

    public void deletePostById(Long id){
        dsl.deleteFrom(post).where(post.ID.eq(id)).execute();
    }

    private PostRecord updatePostRecord(CreatePostDto createPostDto) {
        String[] tags = createTagsField(createPostDto.getTags());
        return dsl.update(post)
                .set(post.POST_TEXT, createPostDto.getPostText())
                .set(post.IMAGE_PATH, createPostDto.getImagePath())
                .set(post.TAGS, tags)
                .set(post.TIME_CHANGED, LocalDateTime.now())
                .set(post.TITLE, createPostDto.getTitle())
                .where(post.ID.eq(createPostDto.getId()))
                .returning()
                .fetchOne();
    }

    private PostRecord createPost(CreatePostDto createPostDto, UUID accountId) {
        String[] tags = createTagsField(createPostDto.getTags());
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime publishTime;
        String type;
        if(createPostDto.getPublishDate() != null) {
            publishTime = getPublicationDate(createPostDto.getPublishDate());
            type = TypePost.QUEUED.toString();
        } else {
            publishTime = timeNow;
            type = TypePost.POSTED.toString();
        }
        PostRecord postRecord = postMapper.insertValues2PostRecord(
                accountId,
                createPostDto.getPostText(),
                timeNow,
                type,
                createPostDto.getTitle(),
                publishTime,
                tags,
                createPostDto.getImagePath(),
                timeNow);

        dsl.executeInsert(postRecord);

        return postRecord;

    }
    public List<TagDto> getTags (String tagName){
        List<TagRecord> tagRecord = dsl.selectFrom(tag)
                .where(tag.NAME.likeIgnoreCase(tagName + "%"))
                .fetch();
        if (!tagRecord.isEmpty()) {
            return postMapper.tagRecordToTagDto(tagRecord);
        } else {
            return null;
        }
    }

    private List<PostRecord> getAllPostRecords(PostSearchDto postSearchDto, UUID accountId) {
        int pageSize = postSearchDto.getSize();
        if(postSearchDto.getWithFriends()) {
            return dsl.select()
                    .from(post)
                    .join(friends)
                    .on(friends.REQUESTED_ACCOUNT_ID.eq(post.AUTHOR_ID))
                    .where(friends.ACCOUNT_FROM_ID.eq(accountId))
                    .and(friends.STATUS_CODE.eq("FRIEND"))
                    .and(post.TYPE.eq(String.valueOf(TypePost.POSTED)))
                    .orderBy(sort(postSearchDto))
                    .limit(pageSize)
                    .fetch()
                    .into(PostRecord.class);
        } else if(postSearchDto.getTags() != null) {
            return dsl.select()
                    .from(post)
                    .where(tagsCondition(postSearchDto.getTags()))
                    .and(post.TYPE.eq(String.valueOf(TypePost.POSTED)))
                    .orderBy(sort(postSearchDto))
                    .limit(pageSize)
                    .fetch()
                    .into(PostRecord.class);
        } else {
            Integer count = postSearchDto.getPage();
            int offset = (count != null) ? count * pageSize : 0;
            return dsl.select()
                    .from(post)
                    .where(post.TYPE.eq(String.valueOf(TypePost.POSTED)))
                    .orderBy(sort(postSearchDto))
                    .limit(pageSize)
                    .offset(offset)
                    .fetch()
                    .into(PostRecord.class);
        }
    }

    private List<PostRecord> getPostRecords(PostSearchDto postSearchDto) {
        return dsl.select()
                .from(post)
                .where(post.AUTHOR_ID.eq(postSearchDto.getAccountIds()))
                .and(post.TYPE.eq(String.valueOf(TypePost.POSTED)))
                .orderBy(sort(postSearchDto))
                .limit(postSearchDto.getSize())
                .fetch()
                .into(PostRecord.class);
    }

    private List<PostRecord> searchPostsByUserId(PostSearchDto postSearchDto, UUID accountId) {
        return dsl.select()
                .from(post)
                .join(friends)
                .on(friends.REQUESTED_ACCOUNT_ID.eq(post.AUTHOR_ID))
                .where(friends.ACCOUNT_FROM_ID.eq(accountId))
                .and(friends.STATUS_CODE.eq("FRIEND"))
                .and(authorIdCondition(postSearchDto.getAuthor()))
                .and(tagsCondition(postSearchDto.getTags()))
                .and(timeCondition(postSearchDto.getDateTo(), postSearchDto.getDateFrom()))
                .and(post.TYPE.eq(String.valueOf(TypePost.POSTED)))
                .orderBy(sort(postSearchDto))
                .limit(postSearchDto.getSize())
                .fetch()
                .into(PostRecord.class);
    }

    private Condition tagsCondition(List<String> tags) {
        Condition tagsCondition = DSL.trueCondition();
        for (String tag : tags) {
            tagsCondition = tagsCondition.and(DSL.coalesce(post.TAGS.cast(String[].class), field("ARRAY['']")).like("%" + tag + "%"));
        }
        return tagsCondition;
    }

    private Condition authorIdCondition(String author) {
        Condition authorIdCondition = DSL.trueCondition();
        Field<UUID> authorIdField = null;
        if (author != null) {
            String[] name = author.split(" ");
            if (name.length == 1) {
                authorIdField = field(DSL.select(account.ID)
                        .from(account)
                        .where(DSL.lower(account.FIRST_NAME).eq(name[0].toLowerCase()))
                        .or(DSL.lower(account.LAST_NAME).eq(name[0].toLowerCase()))
                        .limit(1));
            } else if (name.length >= 2) {
                authorIdField = field(DSL.select(account.ID)
                        .from(account)
                        .where(DSL.lower(account.FIRST_NAME).eq(name[0].toLowerCase()).and(DSL.lower(account.LAST_NAME).eq(name[1].toLowerCase())))
                        .or(DSL.lower(account.FIRST_NAME).eq(name[1].toLowerCase()).and(DSL.lower(account.LAST_NAME).eq(name[0].toLowerCase())))
                        .limit(1));
            }
        }
        if (authorIdField != null) {
            authorIdCondition = post.AUTHOR_ID.in(authorIdField);
        }
        return authorIdCondition;
    }

    private Condition timeCondition(String to, String from) {
        Condition timeCondition = DSL.trueCondition();
        if (from != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            LocalDateTime fromDateTime = LocalDateTime.parse(from, formatter);
            LocalDateTime toDateTime = LocalDateTime.parse(to, formatter);

            timeCondition = post.PUBLISH_DATE.between(fromDateTime, toDateTime);
        }
        return timeCondition;
    }

    private SortField<?> sort (PostSearchDto postSearchDto){
        List<String> querySort = new ArrayList<>(postSearchDto.getSort());
        SortField<?> sortField = null;
        String type = querySort.get(0);
        String sort = querySort.get(1);
        String DESC = "desc";
        String TYPE = "time";
        if(type.equals(TYPE)){
            Field<?> field = post.PUBLISH_DATE;
            sortField = sort.equalsIgnoreCase(DESC) ? field.desc() : field.asc();
        }
        return sortField;
    }

    private LocalDateTime getPublicationDate(String dateTime) {
        Instant instant = Instant.parse(dateTime);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.toLocalDateTime();
    }

    private String[] createTagsField(List<TagDto> tags) {
        if (tags != null && !tags.isEmpty()) {
            for (TagDto tagDto : tags) {
                if (!dsl.fetchExists(DSL.selectFrom(tag).where(tag.NAME.eq(tagDto.getName())))) {
                    addTag(tagDto.getName());
                }
            }
            return tags.stream()
                    .map(TagDto::getName)
                    .toArray(String[]::new);
        } else {
            return new String[0];
        }
    }

    private void updateType(){
        dsl.update(post)
                .set(post.TYPE, TypePost.POSTED.toString())
                .where(post.PUBLISH_DATE.lessThan(LocalDateTime.now()))
                .and(post.TYPE.eq(TypePost.QUEUED.toString()))
                .execute();
    }

    private UUID getUserIdByEmail(String email){
        AccountRecord accountRecord = dsl.selectFrom(account)
                .where(account.EMAIL.eq(email))
                .fetchOne();
        assert accountRecord != null;
        return accountRecord.getId();
    }
    private void addTag(String tag){
        TagRecord tagRecord = new TagRecord();
        tagRecord.setName(tag);
        tagRecord.setIsDeleted(false);
        dsl.executeInsert(tagRecord);
    }
}
