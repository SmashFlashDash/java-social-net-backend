package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.AccountDto;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.mappers.AccountMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final DSLContext dslContext;
    private final Account account = Account.ACCOUNT;
    private final AccountMapper mapper = Mappers.getMapper(AccountMapper.class);

    public void save(AccountDto accountDto) {
        AccountRecord rec = dslContext.newRecord(account, mapper.accountDtoToAccountRecord(accountDto));
        rec.store();
    }

    public AccountDto updateAccount(AccountDto accountDto) {
        AccountRecord accountRecord = dslContext.newRecord(account, mapper.accountDtoToAccountRecord(accountDto));
        accountRecord.update();
        return mapper.accountRecordToAccountDto(accountRecord);
    }

    public Long getIdByEmail(String email) {
        AccountRecord accountRecord = dslContext.selectFrom(account).where(account.EMAIL.eq(email)).fetchOne();
        return accountRecord != null ? accountRecord.getId() : null;
    }

    public Optional<AccountRecord> getAccountByEmail(String email) {
        return dslContext.selectFrom(account).where(account.EMAIL.eq(email)).fetchOptional();
    }

    public Result<Record> getAllAccountsByEmail(String email) {
        return dslContext.select().from(account).where(account.EMAIL.eq(email)).fetch();
    }
}
