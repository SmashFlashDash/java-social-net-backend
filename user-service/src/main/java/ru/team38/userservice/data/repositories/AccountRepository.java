package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.AccountDto;
import ru.team38.common.dto.AccountSearchDto;
import ru.team38.common.dto.PageAccountDto;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.dto.notification.NotificationTypeEnum;
import ru.team38.common.jooq.Tables;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.mappers.AccountMapper;
import ru.team38.common.mappers.NotificationSettingMapper;
import ru.team38.userservice.exceptions.AccountNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final DSLContext dslContext;
    private static final Account ACCOUNT = Account.ACCOUNT;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
    private final NotificationSettingMapper notificationSettingMapper = Mappers.getMapper(NotificationSettingMapper.class);

    public void save(AccountDto accountDto) {
        AccountRecord rec = dslContext.newRecord(ACCOUNT, accountMapper.accountDtoToAccountRecord(accountDto));
        rec.store();
    }

    public AccountDto createAccount(AccountDto accountDto) {
        AccountRecord accountRecord = dslContext.insertInto(Tables.ACCOUNT)
                .set(accountMapper.accountDtoToAccountRecord(accountDto))
                .returning()
                .fetchOne();
        return accountMapper.accountRecordToAccountDto(accountRecord);
    }

    public AccountDto updateAccount(AccountDto accountDto) {
        AccountRecord accountRecord = dslContext.newRecord(ACCOUNT, accountMapper.accountDtoToAccountRecord(accountDto));
        accountRecord.update();
        return accountMapper.accountRecordToAccountDto(accountRecord);
    }

    public UUID getIdByEmail(String email) {
        AccountRecord accountRecord = dslContext.selectFrom(ACCOUNT).where(ACCOUNT.EMAIL.eq(email)).fetchOne();
        return accountRecord != null ? accountRecord.getId() : null;
    }

    public Optional<AccountRecord> getAccountByEmail(String email) {
        return dslContext
                .selectFrom(ACCOUNT)
                .where(ACCOUNT.EMAIL.eq(email))
                .fetchOptional();
    }

    public PageAccountDto findAccount(UUID userId, AccountSearchDto accountSearchDto) {
        PageAccountDto pageAccountDto = new PageAccountDto();

        if (accountSearchDto.getFirstName() != null || accountSearchDto.getLastName() != null) {
            Condition condition = (checkConditionToAccountSearch(userId, accountSearchDto.getMaxBirthDate(),
                    accountSearchDto.getMinBirthDate(), accountSearchDto.getFirstName(),
                    accountSearchDto.getLastName()));
            dslContext.select().from(ACCOUNT)
                    .where(condition).fetch()
                    .map(rec -> accountMapper.accountRecordToAccountDto(rec.into(ACCOUNT)))
                    .forEach(pageAccountDto::setAccount);
        }

        if (pageAccountDto.getContent().isEmpty()
                && (accountSearchDto.getFirstName() != null
                && accountSearchDto.getLastName() != null)) {

                String temp = accountSearchDto.getLastName();
                accountSearchDto.setLastName(null);
                findAccount(userId, accountSearchDto);
                accountSearchDto.setFirstName(null);
                accountSearchDto.setLastName(temp);
                findAccount(userId, accountSearchDto);
        }
        return pageAccountDto;
    }

    private Condition checkConditionToAccountSearch(UUID userId, LocalDate maxBirthDate, LocalDate minBirthDate,
                                                    String firstName, String lastName) {
        Condition condition = ACCOUNT.ID.ne(userId);
        char ch = '%';

        if (maxBirthDate != null) {
            condition = condition.and(ACCOUNT.BIRTH_DATE.le(maxBirthDate));
        }
        if (minBirthDate != null) {
            condition = condition.and(ACCOUNT.BIRTH_DATE.ge(minBirthDate));
        }
        if (firstName != null && lastName != null) {
            condition = condition.and(((ACCOUNT.FIRST_NAME.likeIgnoreCase(ch + firstName + ch))
                    .and(ACCOUNT.LAST_NAME.likeIgnoreCase(ch + lastName + ch)))
                    .or((ACCOUNT.FIRST_NAME.likeIgnoreCase(ch + lastName + ch))
                    .and(ACCOUNT.LAST_NAME.likeIgnoreCase(ch + firstName + ch))));
        }
        if (firstName != null && lastName == null) {
            condition = condition.and((ACCOUNT.FIRST_NAME.likeIgnoreCase(ch + firstName + ch))
                    .or(ACCOUNT.LAST_NAME.likeIgnoreCase(ch + firstName + ch)));
        }
        if (lastName != null && firstName == null) {
            condition = condition.and((ACCOUNT.LAST_NAME.likeIgnoreCase(ch + lastName + ch))
                    .or(ACCOUNT.FIRST_NAME.likeIgnoreCase(ch + lastName + ch)));
        }
        return condition;
    }

    private AccountRecord getAccountRecordById(UUID id) {
        return dslContext
                .selectFrom(Tables.ACCOUNT)
                .where(Account.ACCOUNT.ID.eq(id))
                .fetchOptional()
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id " + id));
    }

    public NotificationSettingDto getNotificationSetting(String username) {
        AccountRecord accountRecord = getAccountByEmail(username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with username " + username));
        return notificationSettingMapper.accountRecordToNotificationSettingDto(accountRecord);
    }

    public NotificationSettingDto updateNotificationSetting(String username, NotificationTypeEnum notificationType,
                                                            Boolean enable) {
        AccountRecord accountRecord = getAccountByEmail(username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with username " + username));
        updateNotificationType(accountRecord, notificationType, enable);
        accountRecord.store();
        return notificationSettingMapper.accountRecordToNotificationSettingDto(accountRecord);
    }

    private void updateNotificationType(AccountRecord accountRecord, NotificationTypeEnum notificationType, Boolean enable) {
        switch (notificationType) {
            case POST -> accountRecord.setEnablePost(enable);
            case POST_COMMENT -> accountRecord.setEnablePostComment(enable);
            case COMMENT_COMMENT -> accountRecord.setEnableCommentComment(enable);
            case MESSAGE -> accountRecord.setEnableMessage(enable);
            case FRIEND_REQUEST -> accountRecord.setEnableFriendRequest(enable);
            case FRIEND_BIRTHDAY -> accountRecord.setEnableFriendBirthday(enable);
            case SEND_EMAIL_MESSAGE -> accountRecord.setEnableSendEmailMessage(enable);
            default -> throw new IllegalArgumentException("Invalid notification type: " + notificationType);
        }
    }

    public NotificationSettingDto setNotificationSetting(UUID accountId) {
        AccountRecord accountRecord = getAccountRecordById(accountId);
        setDefaultNotificationSettings(accountRecord);
        accountRecord.store();
        return notificationSettingMapper.accountRecordToNotificationSettingDto(accountRecord);
    }

    private void setDefaultNotificationSettings(AccountRecord accountRecord) {
        accountRecord.setEnablePost(getOrDefault(accountRecord.getEnablePost(), true));
        accountRecord.setEnablePostComment(getOrDefault(accountRecord.getEnablePostComment(), true));
        accountRecord.setEnableCommentComment(getOrDefault(accountRecord.getEnableCommentComment(), true));
        accountRecord.setEnableMessage(getOrDefault(accountRecord.getEnableMessage(), true));
        accountRecord.setEnableFriendRequest(getOrDefault(accountRecord.getEnableFriendRequest(), true));
        accountRecord.setEnableFriendBirthday(getOrDefault(accountRecord.getEnableFriendBirthday(), true));
        accountRecord.setEnableSendEmailMessage(getOrDefault(accountRecord.getEnableSendEmailMessage(), true));
    }

    private <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
