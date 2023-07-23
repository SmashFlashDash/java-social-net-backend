package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.*;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Friends;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.FriendsRecord;
import ru.team38.common.mappers.AccountMapper;
import ru.team38.common.mappers.FriendDtoMapper;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final DSLContext dsl;
    private final FriendDtoMapper friendDtoMapper = Mappers.getMapper(FriendDtoMapper.class);
    private final Account ACCOUNT = Account.ACCOUNT;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    public int countIncomingFriendRequests(UUID accountId) {
        return dsl.fetchCount(Friends.FRIENDS, Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId)
                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.REQUEST_FROM.name())));
    }

    public List<FriendDto> getIncomingFriendRequests(UUID accountId) {
        return dsl.select()
                .from(Friends.FRIENDS)
                .join(Account.ACCOUNT)
                .on(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID))
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId))
                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.REQUEST_FROM.name()))
                .fetch()
                .map(this::mapToFriendDto);
    }

    private FriendDto mapToFriendDto(Record rec) {
        FriendsRecord friendsRecord = rec.into(Friends.FRIENDS);
        AccountRecord accountRecord = rec.into(Account.ACCOUNT);
        return friendDtoMapper.mapToFriendDto(friendsRecord, accountRecord);
    }

    public List<AccountDto> getFriendsByParameters(UUID accountId, Condition condition, StatusCode statusCode) {
        return dsl.select()
                .from(Account.ACCOUNT)
                .join(Friends.FRIENDS)
                .on(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(ACCOUNT.ID).or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID)))
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId).or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(accountId)))
                .and(Friends.FRIENDS.STATUS_CODE.eq(statusCode.name()))
                .and(condition)
                .fetch()
                .map(record -> accountMapper.accountRecordToAccountDto(record.into(Account.ACCOUNT)));
    }

    private FriendShortDto mapToFriendShortDto(Record record, UUID accountId) {
        FriendsRecord friendsRecord = record.into(Friends.FRIENDS);
        AccountRecord accountRecord = record.into(ACCOUNT);
        FriendShortDto friendShortDto = friendDtoMapper.mapToFriendShortDto(friendsRecord, accountRecord);
        friendShortDto.setId(accountId);
        return friendShortDto;
    }

    public List<UUID> getFriendsIds(UUID accountId, String friendsIds) {
        String subQueryFriendsIds = "select \n" +
                "case when account_from_id = '" + accountId + "' then null else account_from_id end,\n" +
                "case when requested_account_id = '" + accountId + "' then null else requested_account_id end\n" +
                "from socialnet.socialnet.friends f \n" +
                "where account_from_id = '" + accountId + "' or requested_account_id = '" + accountId + "'  \n";
        String subQueryFriendsFriendsIds = "select \n" +
                "case when account_from_id in (" + friendsIds + ") then null else account_from_id end,\n" +
                "case when requested_account_id in (" + friendsIds + ") then null else requested_account_id end\n" +
                "from socialnet.socialnet.friends f \n" +
                "where (account_from_id in (" + friendsIds + ") or requested_account_id in (" + friendsIds + "))\n" +
                "and account_from_id <> '" + accountId + "' \n" +
                "and requested_account_id <> '" + accountId + "'  \n";
        String subQuery = (friendsIds == null) ? subQueryFriendsIds : subQueryFriendsFriendsIds;
        String query = "select value from (\n" +
                "select account_from_id as value from (" + subQuery + ") f\n" +
                "union \n" +
                "select requested_account_id as value from (" + subQuery + ") f\n" +
                ") as result where value is not null;";
        return dsl.fetch(query).map(record -> (UUID) record.getValue("value"));
    }

    public List<UUID> getRecommendationsIds(List<UUID> friendsIds, List<UUID> friendsFriendsIds, Condition condition) {
        return dsl.select(Account.ACCOUNT.ID)
                .from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.notIn(friendsIds))
                .and(Account.ACCOUNT.ID.notIn(friendsFriendsIds))
                .and(condition)
                .fetchInto(UUID.class);
    }

    public List<FriendShortDto> getFinalRecommendations(UUID accountId, List<UUID> finalIds) {
        return dsl.select()
                .from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.in(finalIds))
                .fetch()
                .map(record -> mapToFriendShortDto(record, accountId));
    }
}