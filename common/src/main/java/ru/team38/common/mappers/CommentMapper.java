package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.jooq.tables.records.CommentRecord;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Mapper
public interface CommentMapper {

    @Mapping(source = "time", target = "time", qualifiedByName = "zonedDateTimeToOffsetDateTime", defaultValue = "1970-01-01T00:00:00Z")
    @Mapping(source = "timeChanged", target = "timeChanged", qualifiedByName = "zonedDateTimeToOffsetDateTime", defaultValue = "1970-01-01T00:00:00Z")
    @Mapping(source = "isDeleted", target = "isDeleted", defaultValue = "false")
    @Mapping(source = "isBlocked", target = "isBlocked", defaultValue = "false")
    @Mapping(source = "commentsCount", target = "commentsCount", defaultValue = "0")
    @Mapping(source = "likeAmount", target = "likeAmount", defaultValue = "0")
    @Mapping(source = "myLike", target = "myLike", defaultValue = "false")
    CommentRecord commentDtoToCommentRecord(CommentDto commentDto);

    @Mapping(source = "time", target = "time", qualifiedByName = "offsetDateTimeToZonedDateTime")
    @Mapping(source = "timeChanged", target = "timeChanged", qualifiedByName = "offsetDateTimeToZonedDateTime")
    CommentDto commentRecordToCommentDto(CommentRecord commentRecord);

    @Named("zonedDateTimeToOffsetDateTime")
    default OffsetDateTime zonedDateTimeToOffsetDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : zonedDateTime.toOffsetDateTime();
    }

    @Named("offsetDateTimeToZonedDateTime")
    default ZonedDateTime offsetDateTimeToZonedDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toZonedDateTime();
    }
}
