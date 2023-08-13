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

    LikeRecord map2LikeRecord(LikeDto likeDto);

    LikeDto LikeRecord2likeDto(LikeRecord likeRecord);

    default ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()) : null;
    }
}
