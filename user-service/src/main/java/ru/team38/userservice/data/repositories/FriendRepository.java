package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.FriendDto;
import ru.team38.common.dto.FriendSearchDto;
import ru.team38.common.dto.FriendShortDto;
import ru.team38.common.dto.StatusCode;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Friends;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.FriendsRecord;
import ru.team38.common.mappers.FriendDtoMapper;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final DSLContext dsl;
    private final FriendDtoMapper friendDtoMapper = Mappers.getMapper(FriendDtoMapper.class);
    private final Account ACCOUNT = Account.ACCOUNT;

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

    public List<FriendShortDto> getFriendsByParameters(UUID accountId, FriendSearchDto friendSearchDto) {
        Condition condition = Account.ACCOUNT.ID.ne(accountId);
        if (friendSearchDto.getFirstName() != null) {
            condition = condition.and(Account.ACCOUNT.FIRST_NAME.eq(friendSearchDto.getFirstName()));
        }
        if (friendSearchDto.getCity() != null) {
            condition = condition.and(Account.ACCOUNT.CITY.eq(friendSearchDto.getCity()));
        }
        if (friendSearchDto.getCountry() != null) {
            condition = condition.and(Account.ACCOUNT.COUNTRY.eq(friendSearchDto.getCountry()));
        }
        if (friendSearchDto.getBirthDateFrom() != null) {
            condition = condition.and(Account.ACCOUNT.BIRTH_DATE.le(friendSearchDto.getBirthDateFrom()));
        }
        if (friendSearchDto.getBirthDateTo() != null) {
            condition = condition.and(Account.ACCOUNT.BIRTH_DATE.ge(friendSearchDto.getBirthDateTo()));
        }
        return dsl.select()
                .from(Friends.FRIENDS)
                .join(Account.ACCOUNT)
                .on(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(ACCOUNT.ID).or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID)))
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(accountId).or(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(accountId)))
                .and(Friends.FRIENDS.STATUS_CODE.eq("FRIEND"))
                .and(condition)
                .fetch()
                .map(record -> mapToFriendShortDto(record, accountId));
    }

    private FriendShortDto mapToFriendShortDto(Record record, UUID accountId) {
        FriendsRecord friendsRecord = record.into(Friends.FRIENDS);
        AccountRecord accountRecord = record.into(ACCOUNT);
        FriendShortDto friendShortDto = friendDtoMapper.mapToFriendShortDto(friendsRecord, accountRecord);
        friendShortDto.setId(accountId);
        return friendShortDto;
    }
}