package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.account.TokensDto;
import ru.team38.common.jooq.tables.Tokens;
import ru.team38.common.jooq.tables.records.TokensRecord;
import ru.team38.common.mappers.TokensMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final DSLContext dslContext;
    private static final Tokens tokens = Tokens.TOKENS;
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

    public void deleteInvalidTokens() {
        dslContext.deleteFrom(tokens)
                .where(tokens.VALIDITY.eq(false))
                .execute();
    }

    public void deleteExpiredTokens() {
        dslContext.deleteFrom(tokens)
                .where(tokens.EXPIRATION.lt(LocalDateTime.now(ZoneOffset.UTC)))
                .execute();
    }

    public List<TokensDto> findByDeviceUUID(String deviceUUID) {
        List<TokensRecord> records = dslContext.selectFrom(tokens)
                .where(tokens.DEVICE_UUID.eq(deviceUUID))
                .fetch();
        return records.stream()
                .map(mapper::tokensRecordToTokensDto)
                .toList();
    }
}
