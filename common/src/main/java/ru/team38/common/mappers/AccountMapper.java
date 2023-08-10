package ru.team38.common.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.team38.common.dto.account.AccountDto;
import ru.team38.common.dto.other.StatusCode;
import ru.team38.common.jooq.tables.records.AccountRecord;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDto accountRecordToAccountDto(AccountRecord accountRecord);

    AccountRecord accountDtoToAccountRecord(AccountDto accountDto);

    @AfterMapping
    default void setStatusCode(@MappingTarget AccountDto accountDto) {
        accountDto.setStatusCode(StatusCode.NONE);
    }

    default ZonedDateTime map(LocalDateTime localDateTime) {
        return localDateTime != null ? ZonedDateTime.of(localDateTime, ZoneId.systemDefault()) : null;
    }
}