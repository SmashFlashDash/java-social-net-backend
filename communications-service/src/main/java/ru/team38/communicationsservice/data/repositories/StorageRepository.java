package ru.team38.communicationsservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.storage.FileDto;
import ru.team38.common.jooq.tables.FileStorage;
import ru.team38.common.jooq.tables.records.FileStorageRecord;
import ru.team38.common.mappers.FileStorageMapper;

@Repository
@RequiredArgsConstructor
public class StorageRepository {
    private final DSLContext dsl;
    private final FileStorage FILE_STORAGE = FileStorage.FILE_STORAGE;
    private final FileStorageMapper mapper = Mappers.getMapper(FileStorageMapper.class);

    public void saveNewFileRecord(FileDto fileDto) {
        FileStorageRecord record = dsl.newRecord(FILE_STORAGE, mapper.fileDtoToFileRecord(fileDto));
        record.store();
    }
}
