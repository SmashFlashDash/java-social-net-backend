package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.team38.common.dto.dialog.MessageDto;
import ru.team38.common.dto.dialog.MessageShortDto;
import ru.team38.common.jooq.tables.records.MessageRecord;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper
public interface DialogMapper {
    @Mapping(target = "time", expression = "java(toZonedDateTime(messageRecord.getTime()))")
    MessageDto messageRecordToMessageDto(MessageRecord messageRecord);
    List<MessageDto> messageRecordsToMessagesDto(List<MessageRecord> messageRecords);
    List<MessageShortDto> messageRecordsToMessagesShortDto(List<MessageRecord> messageRecords);
    default ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()) : null;
    }
}