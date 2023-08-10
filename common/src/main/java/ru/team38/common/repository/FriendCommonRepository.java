package ru.team38.common.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.other.StatusCode;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Friends;
import ru.team38.common.jooq.tables.records.AccountRecord;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FriendCommonRepository {
    private final DSLContext DSL;
    private static final Account ACCOUNT = Account.ACCOUNT;
    private static final Friends FRIENDS = Friends.FRIENDS;

    public List<AccountRecord> getFriendAccountsListByAccountId(UUID accountId) {
        return DSL.select().from(FRIENDS).join(ACCOUNT).on(FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID))
                .where(FRIENDS.ACCOUNT_FROM_ID.eq(accountId))
                .and(FRIENDS.STATUS_CODE.eq(StatusCode.FRIEND.name()))
                .fetchInto(ACCOUNT);
    }
}
