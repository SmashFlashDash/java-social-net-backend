package ru.team38.common.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.team38.common.jooq.Tables;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.CitiesRecord;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class AccountCommonRepository {
    private final DSLContext DSL;
    private final Account ACCOUNT = Account.ACCOUNT;

    public Optional<AccountRecord> findAccountByID(UUID uuid) {
        return DSL.selectFrom(ACCOUNT).where(ACCOUNT.ID.eq(uuid)).fetchOptional();
    }

    public List<AccountRecord> findAccountsByCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        return DSL.selectFrom(ACCOUNT)
                .where(month(Tables.ACCOUNT.BIRTH_DATE).eq(currentDate.getMonthValue())
                        .and(day(Tables.ACCOUNT.BIRTH_DATE).eq(currentDate.getDayOfMonth()))
                        .or(day(Tables.ACCOUNT.BIRTH_DATE).eq(currentDate.getDayOfMonth() + 1))
                        .or(day(Tables.ACCOUNT.BIRTH_DATE).eq(currentDate.getDayOfMonth() - 1)))
                .fetch();
    }

    public ZoneId getZoneIdByCityAndCountry(String city, String country){
        CitiesRecord citiesRecord = DSL.select()
                .from(Tables.CITIES)
                .join(Tables.COUNTRIES)
                .on(Tables.CITIES.COUNTRY_ID.eq(Tables.COUNTRIES.COUNTRY_ID))
                .where(Tables.COUNTRIES.COUNTRY_NAME.eq(country))
                .and(Tables.CITIES.CITY_NAME.eq(city))
                .and(Tables.CITIES.ZONE_ID.isNotNull())
                .fetchOneInto(CitiesRecord.class);
        if(citiesRecord == null){
            return ZoneOffset.UTC;
        }
        return ZoneId.of(citiesRecord.getZoneId());
    }
}
