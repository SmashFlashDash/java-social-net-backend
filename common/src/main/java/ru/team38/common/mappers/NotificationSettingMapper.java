package ru.team38.common.mappers;

import org.mapstruct.Mapper;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.jooq.tables.records.AccountRecord;

@Mapper
public interface NotificationSettingMapper {

    NotificationSettingDto accountRecordToNotificationSettingDto(AccountRecord accountRecord);
}
