package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.StatusCode;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.common.jooq.Tables;
import ru.team38.common.jooq.tables.Notification;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.FriendsRecord;
import ru.team38.common.jooq.tables.records.NotificationRecord;
import ru.team38.common.mappers.NotificationMapper;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.day;
import static org.jooq.impl.DSL.month;
@Repository
@RequiredArgsConstructor
public class NotificationRepository {
    private final DSLContext DSL;
    private final Notification NTF = Notification.NOTIFICATION;
    private final NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

    public Integer getNotificationsCountByAccountId(UUID accountId) {
        return DSL.fetchCount(NTF, NTF.RECEIVER_ID.eq(accountId), NTF.IS_READED.isFalse());
    }

    public List<DataTimestampDto> getNotificationsByAccountId(UUID accountId, Integer limit) {
        Result<NotificationRecord> records = getLastNotifications(accountId, limit);
        return records.stream().map(record -> {
            DataTimestampDto data = new DataTimestampDto();
            data.setTimestamp(ZonedDateTime.now());
            data.setData(mapper.notificationRecordToNotificationDto(record));
            return data;
        }).collect(Collectors.toList());
    }

    public void updateNotificationsReadByAccountId(UUID accountId, Integer limit) {
        List<Long> idList = getLastNotifications(accountId, limit).getValues(NTF.ID);
        DSL.update(NTF).set(NTF.IS_READED, true).where(NTF.ID.in(idList)).execute();
    }

    public void addNotificationBirthday(UUID authorId, UUID receiverId, String birthdayMessage) {
        DSL.executeInsert(mapper.mapToNotificationRecordByBirthday(
                authorId,
                receiverId,
                birthdayMessage));
    }
    public List<AccountRecord> findAccountsByCurrentDate() {
        List<AccountRecord> accountRecords = DSL.selectFrom(Tables.ACCOUNT)
                .where(month(Tables.ACCOUNT.BIRTH_DATE).eq(LocalDate.now().getMonthValue()))
                .and(day(Tables.ACCOUNT.BIRTH_DATE).eq(LocalDate.now().getDayOfMonth()))
                .fetch();

        return accountRecords.isEmpty() ? null : accountRecords;
    }

    public List<FriendsRecord> findFriendsByAuthorId(UUID authorId) {
        List<FriendsRecord> friendsRecords = DSL.selectFrom(Tables.FRIENDS)
                .where(Tables.FRIENDS.REQUESTED_ACCOUNT_ID.eq(authorId))
                .and(Tables.FRIENDS.STATUS_CODE.eq(StatusCode.FRIEND.name()))
                .fetch();

        return friendsRecords.isEmpty() ? null : friendsRecords;
    }
    private  Result<NotificationRecord> getLastNotifications(UUID accountId, Integer limit) {
        return DSL.selectFrom(NTF).where(NTF.RECEIVER_ID.eq(accountId), NTF.IS_READED.isFalse())
                .orderBy(NTF.SEND_TIME.asc()).limit(limit).fetch();
    }
}
