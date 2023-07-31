package ru.team38.common.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.notification.NotificationDto;
import ru.team38.common.jooq.tables.Notification;
import ru.team38.common.jooq.tables.records.NotificationRecord;
import ru.team38.common.mappers.NotificationMapper;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationCommonRepository {
    private final DSLContext DSL;
    private final Notification NTF = Notification.NOTIFICATION;
    private final NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

    public void addNotification(NotificationDto ntf) {
        NotificationRecord rec = DSL.newRecord(NTF, mapper.notificationDtoToNotificationRecord(ntf));
        rec.setId(null);
        rec.changed(NTF.ID, false);
        rec.store();
    }

    public List<NotificationRecord> getNotificationsBirthdayForDay(LocalDate date) {
        return DSL.selectFrom(NTF).where(NTF.SEND_TIME.greaterOrEqual(date.atStartOfDay())).fetch();
    }

    public void addNotificationsBatch(List<NotificationDto> notifications) {
        List<NotificationRecord> records = notifications.stream().map(ntfDto -> {
            NotificationRecord rec = mapper.notificationDtoToNotificationRecord(ntfDto);
            rec.setId(null);
            rec.changed(NTF.ID, false);
            return rec;
        }).toList();
        DSL.batchInsert(records).execute();
    }
}
