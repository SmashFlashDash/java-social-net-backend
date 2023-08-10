package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.team38.common.dto.account.TokensDto;
import ru.team38.common.jooq.tables.records.TokensRecord;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper
public interface TokensMapper {

    @Mapping(source = "isValid", target = "validity")
    TokensRecord tokensDtoToTokensRecord(TokensDto tokensDto);

    @Mapping(source = "validity", target = "isValid")
    TokensDto tokensRecordToTokensDto(TokensRecord tokensRecord);

    default ZonedDateTime map(LocalDateTime localDateTime) {
        return localDateTime != null ? ZonedDateTime.of(localDateTime, ZoneId.systemDefault()) : null;
    }
}
