package ru.team38.common.mappers;

import org.mapstruct.*;
import org.mapstruct.Mapper;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.dto.post.ReactionDto;
import ru.team38.common.dto.post.TagDto;
import ru.team38.common.jooq.tables.records.PostRecord;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper
public interface PostMapper {

    @Mapping(target = "time", expression = "java(toZonedDateTime(postRecord.getTime()))")
    @Mapping(target = "timeChanged", expression = "java(toZonedDateTime(postRecord.getTimeChanged()))")
    @Mapping(target = "publishDate", expression = "java(toZonedDateTime(postRecord.getPublishDate()))")
    @Mapping(target = "tags", expression = "java(mapText(postRecord.getTags()))")
    PostDto postRecord2RequestPostDto(PostRecord postRecord);
    default List<TagDto> mapText(String[] text) {
        List<TagDto> tags = new ArrayList<>();
        if (text != null) {
            for (String tag : text) {
                TagDto tagDto = new TagDto();
                tagDto.setName(tag);
                tags.add(tagDto);
            }
        }
        return tags;
    }

    List<PostDto> postRecordToPostDto(List<PostRecord> records);
    @AfterMapping
    default void setMyReactions(@MappingTarget PostDto postDto){postDto.setMyReactions("Вау");}
    @AfterMapping
    default void setReactions(@MappingTarget PostDto postDto){
        ReactionDto reactionDto = new ReactionDto("Так себе", 1);
        List<ReactionDto> list = new ArrayList<>();
        list.add(reactionDto);
        postDto.setReactions(list);
    }

    default ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()) : null;
    }

    default ZonedDateTime toZonedDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime().atZone(ZoneId.systemDefault()) : null;
    }
}



