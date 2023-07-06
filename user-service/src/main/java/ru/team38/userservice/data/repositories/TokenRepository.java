package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.TokensDto;
import ru.team38.common.jooq.tables.Tokens;
import ru.team38.common.jooq.tables.records.TokensRecord;
import ru.team38.common.mappers.TokensMapper;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final DSLContext dslContext;
    private final Tokens tokens = Tokens.TOKENS;
    private final TokensMapper mapper = Mappers.getMapper(TokensMapper.class);

    public void save(TokensDto tokenDto) {
        TokensRecord rec = dslContext.newRecord(tokens, mapper.tokensDtoToTokensRecord(tokenDto));
        rec.store();
    }

    public TokensDto findByToken(String tokenValue) {
        TokensRecord rec = dslContext.selectFrom(tokens)
                .where(tokens.TOKEN.eq(tokenValue))
                .fetchOne();
        return rec != null ? mapper.tokensRecordToTokensDto(rec) : null;
    }

    public void update(TokensDto tokenDto) {
        TokensRecord tokensRecord = dslContext.newRecord(tokens, mapper.tokensDtoToTokensRecord(tokenDto));
        tokensRecord.update();
    }
}