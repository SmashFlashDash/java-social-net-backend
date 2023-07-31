package ru.team38.common.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.jooq.Tables;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.mappers.AccountMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.impl.DSL.day;
import static org.jooq.impl.DSL.month;

@Repository
@RequiredArgsConstructor
public class AccountCommonRepository {
    private final DSLContext DSL;
    private final Account ACCOUNT = Account.ACCOUNT;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    public Optional<AccountRecord> findAccountByID(UUID uuid) {
        return DSL.selectFrom(ACCOUNT).where(ACCOUNT.ID.eq(uuid)).fetchOptional();
    }

    public List<AccountRecord> findAccountsByCurrentDate() {
        return DSL.selectFrom(Tables.ACCOUNT)
                .where(month(Tables.ACCOUNT.BIRTH_DATE).eq(LocalDate.now().getMonthValue()))
                .and(day(Tables.ACCOUNT.BIRTH_DATE).eq(LocalDate.now().getDayOfMonth()))
                .fetch();
    }
}
