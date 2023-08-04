package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.Record;
import org.jooq.*;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.FriendDto;
import ru.team38.common.dto.FriendShortDto;
import ru.team38.common.dto.StatusCode;
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

    public List<Object> getFriendsByParameters(UUID accountId, Condition condition, StatusCode statusCode) {
        return dsl.select()
                .from(Account.ACCOUNT)
                .join(Friends.FRIENDS)
                .on(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(ACCOUNT.ID).or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID)))
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId).or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(accountId)))
                .and(Friends.FRIENDS.STATUS_CODE.eq(statusCode.name()))
                .and(condition)
                .fetch()
                .map(rec -> accountMapper.accountRecordToAccountDto(rec.into(Account.ACCOUNT)));
    }

    public List<Object> getFriendsByParametersTabs(UUID accountId, Condition condition, StatusCode statusCode) {
        return dsl.select()
                .from(Friends.FRIENDS)
                .join(Account.ACCOUNT)
                .on(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(ACCOUNT.ID).or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID)))
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId).or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(accountId)))
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

    public List<UUID> getFriendsIds(UUID accountId) {
        return dsl.select(Friends.FRIENDS.ACCOUNT_FROM_ID.as("value"))
                .from(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId)
                        .or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(accountId)))
                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.FRIEND.name()))
                .union(
                        dsl.select(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.as("value"))
                                .from(Friends.FRIENDS)
                                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId)
                                        .or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(accountId)))
                                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.FRIEND.name()))
                )
                .fetch()
                .into(UUID.class);
    }

    public List<UUID> getFriendsFriendsIds(UUID accountId, List<UUID> friendsIds) {
        return dsl.select(Friends.FRIENDS.ACCOUNT_FROM_ID.as("value"))
                .from(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.in(friendsIds)
                        .or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.in(friendsIds)))
                .and(Friends.FRIENDS.ACCOUNT_FROM_ID.ne(accountId))
                .and(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.ne(accountId))
                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.FRIEND.name()))
                .and(Friends.FRIENDS.ACCOUNT_FROM_ID.notIn(friendsIds))
                .union(
                        dsl.select(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.as("value"))
                                .from(Friends.FRIENDS)
                                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.in(friendsIds)
                                        .or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.in(friendsIds)))
                                .and(Friends.FRIENDS.ACCOUNT_FROM_ID.ne(accountId))
                                .and(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.ne(accountId))
                                .and(Friends.FRIENDS.STATUS_CODE.eq(StatusCode.FRIEND.name()))
                                .and(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.notIn(friendsIds))
                )
                .fetch()
                .into(UUID.class);
    }

    public List<UUID> getRecommendationsIds(List<UUID> friendsIds, List<UUID> friendsFriendsIds, Condition condition) {
        return dsl.select(Account.ACCOUNT.ID)
                .from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.notIn(friendsIds))
                .and(Account.ACCOUNT.ID.notIn(friendsFriendsIds))
                .and(condition)
                .fetchInto(UUID.class);
    }

    public List<FriendShortDto> getFinalRecommendations(List<UUID> finalIds) {
        return dsl.select()
                .from(Account.ACCOUNT)
                .where(Account.ACCOUNT.ID.in(finalIds))
                .fetch()
                .map(this::mapToFriendShortDto);
    }

    public FriendShortDto blockAccount(String initiatorUsername, UUID accountToBlockId) {
        AccountRecord initiatorAccountRecord = getAccountRecordByUsername(initiatorUsername);

        FriendsRecord friendsRecord = dsl.selectFrom(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(initiatorAccountRecord.getId()))
                .and(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(accountToBlockId))
                .fetchOne();

        FriendDto friendDto;
        if (friendsRecord != null) {
            if (friendsRecord.getStatusCode().equals(StatusCode.BLOCKED.name())) {
                throw new IllegalArgumentException("User is already blocked " + friendsRecord.getRequestedAccountId());
            }
            friendDto = friendDtoMapper.mapToFriendDto(friendsRecord, initiatorAccountRecord);
            friendDto.setPreviousStatus(friendDto.getStatusCode());
            friendDto.setStatusCode(StatusCode.BLOCKED);
        } else {
            friendDto = FriendDto.builder()
                    .accountFrom(initiatorAccountRecord.getId())
                    .accountTo(accountToBlockId)
                    .previousStatus(StatusCode.NONE)
                    .statusCode(StatusCode.BLOCKED)
                    .build();
        }
        friendsRecord = friendDtoMapper.friendDtoToFriendsRecord(friendDto);
        dsl.attach(friendsRecord);
        upsert(dsl, friendsRecord);

        AccountRecord blockedAccountRecord = dsl.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(accountToBlockId))
                .fetchOne();

        return friendDtoMapper.mapToFriendShortDto(friendsRecord, blockedAccountRecord);
    }

    public FriendShortDto unblockAccount(String initiatorUsername, UUID accountToUnblockId) {
        AccountRecord initiatorAccountRecord = getAccountRecordByUsername(initiatorUsername);

        FriendsRecord friendsRecord = dsl.selectFrom(Friends.FRIENDS)
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(initiatorAccountRecord.getId()))
                .and(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(accountToUnblockId))
                .fetchOne();

        if (friendsRecord != null && StatusCode.BLOCKED.name().equals(friendsRecord.getStatusCode())) {
            FriendDto friendDto = friendDtoMapper.mapToFriendDto(friendsRecord, initiatorAccountRecord);
            friendDto.setStatusCode(friendDto.getPreviousStatus());
            friendDto.setPreviousStatus(StatusCode.BLOCKED);
            friendsRecord = friendDtoMapper.friendDtoToFriendsRecord(friendDto);
            dsl.attach(friendsRecord);
            friendsRecord.update();
        } else {
            throw new IllegalArgumentException("The specified account is not blocked or does not exist.");
        }

        AccountRecord unblockedAccountRecord = dsl.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(accountToUnblockId))
                .fetchOne();

        return friendDtoMapper.mapToFriendShortDto(friendsRecord, unblockedAccountRecord);
    }

    private AccountRecord getAccountRecordByUsername(String initiatorUsername) {
        return dsl.selectFrom(ACCOUNT)
                .where(ACCOUNT.EMAIL.eq(initiatorUsername))
                .fetchOptional()
                .orElseThrow(() -> new AccountNotFoundException("Account not found with username " + initiatorUsername));
    }

    private <R extends UpdatableRecord<R>> void upsert(final DSLContext dslContext, final UpdatableRecord<R> rec) {
        dslContext.insertInto(rec.getTable())
                .set(rec)
                .onDuplicateKeyUpdate()
                .set(rec)
                .execute();
    }
}
