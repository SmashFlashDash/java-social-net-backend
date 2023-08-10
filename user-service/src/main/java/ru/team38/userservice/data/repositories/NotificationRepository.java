package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.notification.NotificationDto;
import ru.team38.common.jooq.tables.Notification;
import ru.team38.common.jooq.tables.records.NotificationRecord;
import ru.team38.common.mappers.NotificationMapper;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {
    private final DSLContext DSL;
    private final Notification NTF = Notification.NOTIFICATION;
    private final NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

    public Integer getNotificationsCountByAccountId(UUID accountId) {
        return DSL.fetchCount(NTF, NTF.RECEIVER_ID.eq(accountId), NTF.IS_READED.isFalse());
    }

    public List<NotificationDto> getNotificationsByAccountId(UUID accountId, Integer limit) {
        return mapper.notificationRecordsToNotificationDtos(getLastNotifications(accountId, limit));
    }

    public void updateNotificationsReadByAccountId(UUID accountId, Integer limit) {
        List<Long> idList = getLastNotifications(accountId, limit).getValues(NTF.ID);
        DSL.update(NTF).set(NTF.IS_READED, true).where(NTF.ID.in(idList)).execute();
    }

    private  Result<NotificationRecord> getLastNotifications(UUID accountId, Integer limit) {
        return DSL.selectFrom(NTF).where(NTF.RECEIVER_ID.eq(accountId), NTF.IS_READED.isFalse())
                .orderBy(NTF.SEND_TIME.desc()).limit(limit).fetch();
    }
}
