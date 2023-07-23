package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.AccountDto;
import ru.team38.common.dto.AccountResultSearchDto;
import ru.team38.common.dto.AccountSearchDto;
import ru.team38.common.jooq.Tables;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.mappers.AccountMapper;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final DSLContext dslContext;
    private final Account account = Account.ACCOUNT;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    public void save(AccountDto accountDto) {
        AccountRecord rec = dslContext.newRecord(account, accountMapper.accountDtoToAccountRecord(accountDto));
        rec.store();
    }

    public AccountDto createAccount(AccountDto accountDto) {
        AccountRecord accountRecord = dslContext.insertInto(Tables.ACCOUNT)
                .set(accountMapper.accountDtoToAccountRecord(accountDto))
                .returning()
                .fetchOne();
        return accountMapper.accountRecordToAccountDto(accountRecord);
    }

    public AccountDto updateAccount(AccountDto accountDto) {
        AccountRecord accountRecord = dslContext.newRecord(account, accountMapper.accountDtoToAccountRecord(accountDto));
        accountRecord.update();
        return accountMapper.accountRecordToAccountDto(accountRecord);
    }

    public UUID getIdByEmail(String email) {
        AccountRecord accountRecord = dslContext.selectFrom(account).where(account.EMAIL.eq(email)).fetchOne();
        return accountRecord != null ? accountRecord.getId() : null;
    }

    public Optional<AccountRecord> getAccountByEmail(String email) {
        return dslContext.selectFrom(account).where(account.EMAIL.eq(email)).fetchOptional();
    }

    public Result<Record> getAllAccountsByEmail(String email) {
        return dslContext.select().from(account).where(account.EMAIL.eq(email)).fetch();
    }

    public AccountResultSearchDto findAccount(UUID userId, AccountSearchDto accountSearchDto) {
        AccountResultSearchDto accountResultSearchDto = new AccountResultSearchDto();
        if (accountSearchDto.getFirstName() != null || accountSearchDto.getLastName() != null) {
            Condition condition = (checkConditionToAccountSearch(userId, accountSearchDto.getMaxBirthDate(),
                    accountSearchDto.getMinBirthDate(), accountSearchDto.getFirstName(),
                    accountSearchDto.getLastName()));
            dslContext.select().from(account)
                    .where(condition).fetch()
                    .map(rec -> accountMapper.accountRecordToAccountDto(rec.into(account)))
                    .forEach(accountResultSearchDto::setAccount);
        }
        return accountResultSearchDto;
    }

    private Condition checkConditionToAccountSearch(UUID userId, LocalDate maxBirthDate, LocalDate minBirthDate,
                                                    String firstName, String lastName) {
        Condition condition = account.ID.ne(userId);
        if (maxBirthDate != null) {
            condition = condition.and(account.BIRTH_DATE.le(maxBirthDate));
        }
        if (minBirthDate != null) {
            condition = condition.and(account.BIRTH_DATE.ge(minBirthDate));
        }
        if (firstName != null && lastName != null) {
            condition = condition.and(account.FIRST_NAME.eq(firstName)).and(account.LAST_NAME.eq(lastName));
        }
        if (firstName != null && lastName == null) {
            condition = condition.and(account.FIRST_NAME.eq(firstName));
        }
        if (lastName != null && firstName == null) {
            condition = condition.and(account.LAST_NAME.eq(lastName));
        }
        return condition;
    }
}
