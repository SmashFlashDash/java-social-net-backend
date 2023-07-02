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
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.PostRecord;
import ru.team38.common.mappers.PostMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.*;


@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final DSLContext dsl;
    private final Post post = Post.POST;
    private final Friends friends = Friends.FRIENDS;
    private final Account account = Account.ACCOUNT;
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    public List<PostDto> getListPost (PostSearchDto postSearchDto, String email){
        Long accountId = emailUser2idUser(email);
        updateType();
        if(postSearchDto.getIsDeleted()){
            return null;
        }
        if(postSearchDto.getAccountIds() != null){
            return postMapper.postRecordToPostDto(postsByAccount(postSearchDto));
        }
        if(postSearchDto.getDateTo() != null ){
            return postMapper.postRecordToPostDto(postsSearch(postSearchDto, accountId));
        }
        return postMapper.postRecordToPostDto(allPosts(postSearchDto, accountId));
    }
    public PostDto getCreatePost(CreatePostDto createPostDto, String email) {
        Long accountId = emailUser2idUser(email);
        return postMapper.postRecord2RequestPostDto(createPost(createPostDto, accountId));
    }
    public PostDto getUpdatePost(CreatePostDto createPostDto) {
        return postMapper.postRecord2RequestPostDto(updatePost(createPostDto));
    }
    public PostDto getPostById(Long id){
        Record postById = dsl.select()
                .from(post)
                .where(post.ID.eq(id))
                .fetchOne();
        return postMapper.postRecord2RequestPostDto((PostRecord) postById);
    }
    public void deletePostById(Long id){
        dsl.deleteFrom(post).where(post.ID.eq(id)).execute();
    }

    private PostRecord updatePost(CreatePostDto createPostDto) {
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

    private PostRecord createPost(CreatePostDto createPostDto, Long accountId) {
        String[] tags = createTagsField(createPostDto.getTags());
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime publishTime;
        String type;
        if(createPostDto.getPublishDate() != null){
            publishTime = getPublishDate(createPostDto.getPublishDate());
            type = TypePost.QUEUED.toString();
        }else{
            publishTime = timeNow;
            type = TypePost.POSTED.toString();
        }
        return dsl.insertInto(post,
                        post.IMAGE_PATH,
                        post.POST_TEXT,
                        post.TAGS,
                        post.TITLE,
                        post.TYPE,
                        post.IS_DELETED,
                        post.TIME,
                        post.TIME_CHANGED,
                        post.AUTHOR_ID,
                        post.IS_BLOCKED,
                        post.COMMENTS_COUNT,
                        post.LIKE_AMOUNT,
                        post.MY_LIKE,
                        post.PUBLISH_DATE)
                .values(createPostDto.getImagePath(),
                        createPostDto.getPostText(),
                        tags,
                        createPostDto.getTitle(),
                        type,
                        false,
                        timeNow,
                        timeNow,
                        accountId,
                        false,
                        0,
                        0,
                        false,
                        publishTime)
                .returning()
                .fetchOne();

    }

    private List<PostRecord> allPosts(PostSearchDto postSearchDto, Long accountId) {
        int pageSize = postSearchDto.getSize();
        if (postSearchDto.getWithFriends()) {
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
        }else if(postSearchDto.getTags() != null){
            return dsl.select()
                    .from(post)
                    .where(tagsCondition(postSearchDto.getTags()))
                    .and(post.TYPE.eq(String.valueOf(TypePost.POSTED)))
                    .orderBy(sort(postSearchDto))
                    .limit(pageSize)
                    .fetch()
                    .into(PostRecord.class);
        }else {
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
    private List<PostRecord> postsByAccount(PostSearchDto postSearchDto) {
        return dsl.select()
                .from(post)
                .where(post.AUTHOR_ID.eq(postSearchDto.getAccountIds()))
                .and(post.TYPE.eq(String.valueOf(TypePost.POSTED)))
                .orderBy(sort(postSearchDto))
                .limit(postSearchDto.getSize())
                .fetch()
                .into(PostRecord.class);
    }
    private List<PostRecord> postsSearch(PostSearchDto postSearchDto, Long accountId) {
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
        Field<Long> authorIdField = null;
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
    private LocalDateTime getPublishDate(String dateTime) {
        Instant instant = Instant.parse(dateTime);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.toLocalDateTime();
    }

    private String[] createTagsField(List<TagDto> tags) {
        if (tags != null && !tags.isEmpty()) {
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
    private Long emailUser2idUser(String email){
        AccountRecord accountRecord = dsl.selectFrom(account)
                .where(account.EMAIL.eq(email))
                .fetchOne();
        assert accountRecord != null;
        return accountRecord.getId();
    }
}
