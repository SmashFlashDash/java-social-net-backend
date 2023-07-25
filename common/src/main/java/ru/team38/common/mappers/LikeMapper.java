package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.common.jooq.tables.records.LikeRecord;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface LikeMapper {
    @Mapping(target = "time", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "id", expression = "java(id())")
    LikeRecord map2LikeRecord(String reactionType, String type, UUID authorId, UUID itemId);
    List<LikeDto> map2LikeDtoList(List<LikeRecord> likeRecord);

    LikeDto LikeRecord2likeDto(LikeRecord likeRecord);

    default UUID id() {
        return UUID.randomUUID();
    }
    default ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()) : null;
    }
}
