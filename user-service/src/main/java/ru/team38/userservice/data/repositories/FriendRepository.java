package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.UpdatableRecord;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.friend.FriendDto;
import ru.team38.common.dto.friend.FriendShortDto;
import ru.team38.common.dto.other.StatusCode;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Friends;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.FriendsRecord;
import ru.team38.common.mappers.AccountMapper;
import ru.team38.common.mappers.FriendDtoMapper;
import ru.team38.userservice.exceptions.AccountNotFoundException;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final DSLContext dsl;
    private final FriendDtoMapper friendDtoMapper = Mappers.getMapper(FriendDtoMapper.class);
    private static final Account ACCOUNT = Account.ACCOUNT;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
    private static final List<String> STATUSES_TO_EXCLUDE = List.of(
            StatusCode.BLOCKED.name(),
            StatusCode.REJECTING.name(),
            StatusCode.DECLINED.name(),
            StatusCode.REQUEST_FROM.name(),
            StatusCode.REQUEST_TO.name(),
            StatusCode.WATCHING.name(),
            StatusCode.FRIEND.name()
    );

    public int countIncomingFriendRequests(UUID accountId) {
        return dsl.fetchCount(Friends.FRIENDS, Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId)
                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.REQUEST_FROM.name())));
    }

    public List<Object> getFriendsByParameters(UUID accountId, Condition condition, StatusCode statusCode) {
        return dsl.select()
                .from(Account.ACCOUNT)
                .join(Friends.FRIENDS)
                .on(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID))
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId))
                .and(Friends.FRIENDS.STATUS_CODE.eq(statusCode.name()))
                .and(condition)
                .fetch()
                .map(rec -> accountMapper.accountRecordToAccountDto(rec.into(Account.ACCOUNT)));
    }

    public List<Object> getFriendsByParametersTabs(UUID accountId, Condition condition, StatusCode statusCode) {
        return dsl.select()
                .from(Friends.FRIENDS)
                .join(Account.ACCOUNT)
                .on(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID))
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId))
                .and(Friends.FRIENDS.STATUS_CODE.eq(statusCode.name()))
                .and(condition)
                .fetch()
                .map(this::mapToFriendShortDto);
    }

    private FriendShortDto mapToFriendShortDto(Record rec) {
        FriendsRecord friendsRecord = rec.into(Friends.FRIENDS);
        AccountRecord accountRecord = rec.into(ACCOUNT);
        FriendShortDto friendShortDto = friendDtoMapper.mapToFriendShortDto(friendsRecord, accountRecord);
        friendShortDto.setId(friendShortDto.getFriendId());
        return friendShortDto;
    }

    public List<UUID> getFriendsIds(UUID id) {
        return dsl.select(Friends.FRIENDS.REQUESTED_ACCOUNT_ID)
                .from(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(id))
                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.FRIEND.name()))
                .fetch()
                .into(UUID.class);
    }

    public List<UUID> getFriendshipRequestedIds(UUID userId) {
        return dsl.select(Friends.FRIENDS.REQUESTED_ACCOUNT_ID)
                .from(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(userId))
                .and(Friends.FRIENDS.STATUS_CODE.in(StatusCode.REQUEST_TO.name(), StatusCode.REQUEST_FROM.name()))
                .fetch()
                .into(UUID.class);
    }

    public List<UUID> getBlockedAccountIds(UUID userId) {
        return dsl.select(Friends.FRIENDS.REQUESTED_ACCOUNT_ID)
                .from(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(userId))
                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.BLOCKED.name()))
                .union(
                        dsl.select(Friends.FRIENDS.ACCOUNT_FROM_ID)
                                .from(Friends.FRIENDS)
                                .where(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(userId))
                                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.BLOCKED.name()))
                )
                .fetch()
                .into(UUID.class);
    }

    public List<UUID> getFriendsFriendsIds(UUID id, List<UUID> friendsIds) {
        return dsl.select(Friends.FRIENDS.REQUESTED_ACCOUNT_ID)
                .from(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.in(friendsIds)
                .and(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.ne(id))
                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.FRIEND.name())))
                .except(
                        dsl.select(Friends.FRIENDS.ACCOUNT_FROM_ID.as("value"))
                                .from(Friends.FRIENDS)
                                .where(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(id))
                                .and(Friends.FRIENDS.STATUS_CODE.in(STATUSES_TO_EXCLUDE))
                                .union(
                                        dsl.select(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.as("value"))
                                                .from(Friends.FRIENDS)
                                                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(id))
                                                .and(Friends.FRIENDS.STATUS_CODE.in(STATUSES_TO_EXCLUDE))))
                .fetch()
                .into(UUID.class);
    }

    public List<UUID> getRecommendationsIds(UUID id, List<UUID> friendsIds, List<UUID> friendsFriendsIds, Condition condition) {
        return dsl.select(Account.ACCOUNT.ID)
                .from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.notIn(friendsIds))
                .and(Account.ACCOUNT.ID.notIn(friendsFriendsIds))
                .and(condition)
                .except(
                        dsl.select(Friends.FRIENDS.ACCOUNT_FROM_ID.as("value"))
                                .from(Friends.FRIENDS)
                                .where(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(id))
                                .and(Friends.FRIENDS.STATUS_CODE.in(STATUSES_TO_EXCLUDE))
                                .union(
                                        dsl.select(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.as("value"))
                                                .from(Friends.FRIENDS)
                                                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(id))
                                                .and(Friends.FRIENDS.STATUS_CODE.in(STATUSES_TO_EXCLUDE))))
                .fetchInto(UUID.class);
    }

    public List<FriendShortDto> getFinalRecommendations(List<UUID> finalIds) {
        return dsl.select()
                .from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.in(finalIds))
                .fetch()
                .map(this::mapToFriendShortDto);
    }

    public FriendShortDto updateRecord(UUID sourceId, UUID targetId, StatusCode statusCode) {
        AccountRecord sourceAccountRecord = getAccountRecordById(sourceId);
        FriendsRecord sourceFriendRecord = getFriendRecordByIds(sourceId, targetId);

        FriendDto initiatorFriendDto;
        initiatorFriendDto = friendDtoMapper.mapToFriendDto(sourceFriendRecord, sourceAccountRecord);
        initiatorFriendDto.setPreviousStatus(initiatorFriendDto.getStatusCode());
        initiatorFriendDto.setStatusCode(statusCode);
        sourceFriendRecord = friendDtoMapper.friendDtoToFriendsRecord(initiatorFriendDto);

        dsl.attach(sourceFriendRecord);
        upsert(dsl, sourceFriendRecord);

        AccountRecord targetAccountRecord = getAccountRecordById(targetId);
        FriendsRecord targetFriendRecord = getFriendRecordByIds(targetId, sourceId);
        return friendDtoMapper.mapToFriendShortDto(targetFriendRecord, targetAccountRecord);
    }

    public FriendShortDto insertRecord(UUID sourceId, UUID targetId, StatusCode statusCode) {
        FriendsRecord sourceFriendRecord = new FriendsRecord();
        sourceFriendRecord.setStatusCode(statusCode.name());
        sourceFriendRecord.setAccountFromId(sourceId);
        sourceFriendRecord.setRequestedAccountId(targetId);
        sourceFriendRecord.setPreviousStatus(StatusCode.NONE.name());

        dsl.attach(sourceFriendRecord);
        upsert(dsl, sourceFriendRecord);

        AccountRecord targetAccountRecord = getAccountRecordById(targetId);
        FriendsRecord targetFriendRecord = getFriendRecordByIds(targetId, sourceId);
        return friendDtoMapper.mapToFriendShortDto(targetFriendRecord, targetAccountRecord);
    }

    public AccountRecord getAccountRecordById(UUID id) {
        return dsl.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id))
                .fetchOptional()
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID " + id));
    }

    public FriendsRecord getFriendRecordByIds(UUID sourceId, UUID targetId) {
        return dsl.selectFrom(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(sourceId))
                .and(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(targetId))
                .fetchOne();
    }

    private <R extends UpdatableRecord<R>> void upsert(final DSLContext dslContext, final UpdatableRecord<R> rec) {
        dslContext.insertInto(rec.getTable())
                .set(rec)
                .onDuplicateKeyUpdate()
                .set(rec)
                .execute();
    }
}
