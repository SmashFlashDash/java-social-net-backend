package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.team38.common.dto.TokensDto;
import ru.team38.common.jooq.tables.records.TokensRecord;


@Mapper
public interface TokensMapper {

    @Mapping(source = "isValid", target = "validity")
    TokensRecord tokensDtoToTokensRecord(TokensDto tokensDto);

    @Mapping(source = "validity", target = "isValid")
    TokensDto tokensRecordToTokensDto(TokensRecord tokensRecord);
}