package ru.team38.communicationsservice.services.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.dto.other.PageableDto;
import ru.team38.common.dto.other.SortDto;
import ru.team38.common.dto.post.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
                .time(timeNow)
                .type(type)
                .tags(tags)
                .build();
    }

    public PageResponseDto<PostDto> createContentPostDto(List<PostDto> posts, Pageable pageable, List<String> sortList) {
        List<PostDto> sortedPosts = posts.stream().sorted(comparator(sortList)).toList();

        boolean isLast = sortedPosts.size() <= pageable.getPageSize();
        int totalElements = sortedPosts.size();

        if (!isLast) {
            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), totalElements);
            sortedPosts = sortedPosts.subList(startIndex, endIndex);
        }

        SortDto sortDto = new SortDto(pageable.getSort().isUnsorted(), pageable.getSort().isSorted(),
                pageable.getSort().isEmpty());

        int totalPages = (sortedPosts.size() + pageable.getPageSize() - 1) / pageable.getPageSize();
        boolean isFirst = pageable.getPageNumber() == 0;
        boolean isEmpty = sortedPosts.isEmpty();

        PageableDto pageableDto = new PageableDto(sortDto, pageable.getPageNumber(), pageable.getPageSize(),
                (int) pageable.getOffset(), pageable.isUnpaged(), pageable.isPaged());

        return PageResponseDto.<PostDto>builder()
                .number(pageable.getPageNumber())
                .numberOfElements(sortedPosts.size())
                .totalElements(totalElements)
                .size(pageable.getPageSize())
                .totalPages(totalPages)
                .pageable(pageableDto)
                .first(isFirst)
                .empty(isEmpty)
                .content(sortedPosts)
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
    private Comparator<PostDto> comparator(List<String> sortList){
        return (o1, o2) -> {
            for (String sort : sortList) {
                String[] parts = sort.split(",");
                String fieldName = parts[0];
                boolean isDesc = parts.length > 1 && parts[1].equalsIgnoreCase("desc");

                if (fieldName.equalsIgnoreCase("time")) {
                    int cmp = o1.getPublishDate().compareTo(o2.getPublishDate());
                    if (cmp != 0) {
                        return isDesc ? cmp : -cmp;
                    }
                }
            }
            return 0;
        };
    }
}
