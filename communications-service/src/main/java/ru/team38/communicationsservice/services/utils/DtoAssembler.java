package ru.team38.communicationsservice.services.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.team38.common.dto.comment.PageableDto;
import ru.team38.common.dto.comment.SortDto;
import ru.team38.common.dto.post.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
@Component
public class DtoAssembler {
    public InsertPostDto createInsertPostDto(CreatePostDto createPostDto){
        String[] tags = createTagsField(createPostDto.getTags());
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime publishDate;
        String type;
        if (createPostDto.getPublishDate() != null) {
            publishDate = getPublicationDate(createPostDto.getPublishDate());
            type = PostType.QUEUED.toString();
        } else {
            publishDate = timeNow;
            type = PostType.POSTED.toString();
        }
        return InsertPostDto.builder()
                .imagePath(createPostDto.getImagePath())
                .postText(createPostDto.getPostText())
                .title(createPostDto.getTitle())
                .publishDate(publishDate)
                .timeChanged(timeNow)
                .time(timeNow)
                .type(type)
                .tags(tags)
                .build();
    }
    public ContentPostDto createContentPostDto(List<PostDto> posts, Pageable pageable) {
        boolean isLast = posts.size() <= pageable.getPageSize();
        int totalElements = posts.size();

        if (!isLast) {
            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), totalElements);
            posts = posts.subList(startIndex, endIndex);
        }

        SortDto sortDto = new SortDto(pageable.getSort().isUnsorted(), pageable.getSort().isSorted(),
                pageable.getSort().isEmpty());

        int totalPages = (posts.size() + pageable.getPageSize() - 1) / pageable.getPageSize();
        boolean isFirst = pageable.getPageNumber() == 0;
        boolean isEmpty = posts.isEmpty();

        PageableDto pageableDto = new PageableDto(sortDto, pageable.getPageNumber(), pageable.getPageSize(),
                (int) pageable.getOffset(), pageable.isUnpaged(), pageable.isPaged());

        return ContentPostDto.builder()
                .number(pageable.getPageNumber())
                .numberOfElements(posts.size())
                .totalElements(totalElements)
                .size(pageable.getPageSize())
                .totalPages(totalPages)
                .pageable(pageableDto)
                .first(isFirst)
                .empty(isEmpty)
                .content(posts)
                .sort(sortDto)
                .last(isLast)
                .build();
    }
    private LocalDateTime getPublicationDate(String dateTime) {
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
}
