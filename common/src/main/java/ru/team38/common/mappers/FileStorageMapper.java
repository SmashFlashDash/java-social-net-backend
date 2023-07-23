package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.team38.common.dto.storage.FileDto;
import ru.team38.common.jooq.tables.records.FileStorageRecord;

@Mapper
public interface FileStorageMapper {
    FileStorageMapper INSTANCE = Mappers.getMapper(FileStorageMapper.class);

    FileDto fileRecordToFileDto(FileStorageRecord fileStorageRecord);

    FileStorageRecord fileDtoToFileRecord(FileDto fileDto);
}
