package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.team38.common.dto.like.ReactionDto;
import ru.team38.common.dto.post.*;

import ru.team38.common.jooq.tables.records.PostRecord;
import ru.team38.common.jooq.tables.records.TagRecord;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.*;

@Mapper
public interface PostMapper {
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "isBlocked", constant = "false")
    @Mapping(target = "commentsCount", constant = "0")
    @Mapping(target = "likeAmount", constant = "0")
    @Mapping(target = "myLike", constant = "false")
    @Mapping(target = "id", expression = "java(id())")
    PostRecord InsertPostDto2PostRecord(InsertPostDto insertPostDto, UUID authorId);

    @Mapping(target = "tags", expression = "java(mapText(postRecord.getTags()))")
    @Mapping(target = "reactions", expression = "java(mapReactions(postRecord.getReactions()))")
    PostDto postRecord2PostDto(PostRecord postRecord);

    @Mapping(target = "isDeleted", constant = "false")
    TagRecord tagNameToTagRecord(String name);

    List<TagDto> tagRecordToTagDto(List<TagRecord> tagRecord);

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
    default List<ReactionDto> mapReactions(String[] reactionArray) {
        List<ReactionDto> reactions = new ArrayList<>();
        if (reactionArray == null) {
            return Collections.emptyList();
        }
        for (String reactionString : reactionArray) {
            String[] parts = reactionString.split(" ");
            if (parts.length == 2) {
                String reactionType = parts[0];
                int count = Integer.parseInt(parts[1]);
                ReactionDto reactionDto = new ReactionDto(reactionType, count);
                reactions.add(reactionDto);
            }
        }

        return reactions;
    }
    default UUID id() {
        return UUID.randomUUID();
    }

    default ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()) : null;
    }
}
