package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.team38.common.jooq.tables.Notification;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {
    private final DSLContext DSL;
    private final Notification NTF = Notification.NOTIFICATION;

    public Integer getNotificationsCountByUserId(UUID accountId) {
        return DSL.fetchCount(NTF, NTF.RECEIVER_ID.eq(accountId), NTF.IS_READED.isFalse());
    }
}
