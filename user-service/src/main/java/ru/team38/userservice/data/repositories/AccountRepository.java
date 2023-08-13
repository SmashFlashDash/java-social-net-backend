package ru.team38.userservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.account.AccountDto;
import ru.team38.common.dto.account.AccountSearchDto;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.dto.notification.NotificationTypeEnum;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.jooq.Tables;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Friends;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.mappers.AccountMapper;
import ru.team38.common.mappers.NotificationSettingMapper;
import ru.team38.userservice.exceptions.AccountNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final DSLContext dslContext;
    private static final Account ACCOUNT = Account.ACCOUNT;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
    private final NotificationSettingMapper notificationSettingMapper = Mappers.getMapper(NotificationSettingMapper.class);

    private final FriendRepository friendRepository;

    public AccountDto save(AccountDto accountDto) {
        AccountRecord rec = dslContext.newRecord(ACCOUNT, accountMapper.accountDtoToAccountRecord(accountDto));
        rec.store();
        return accountMapper.accountRecordToAccountDto(rec);
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

    public Optional<AccountDto> getAccountDtoByEmail(String email) {
        return getAccountByEmail(email)
                .map(accountMapper::accountRecordToAccountDto);
    }

    public Optional<AccountDto> getAccountDtoById(UUID id) {
        return dslContext
                .selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id))
                .fetchOptional()
                .map(accountMapper::accountRecordToAccountDto);
    }

    public PageResponseDto<AccountDto> findAccount(UUID userId, AccountSearchDto accountSearchDto) {
        PageResponseDto<AccountDto> pageResponseDto = new PageResponseDto<>();

        if (accountSearchDto.getFirstName() != null || accountSearchDto.getLastName() != null) {
            Condition condition = (getConditionToAccountSearch(userId, accountSearchDto, false));
            dslContext.select().from(ACCOUNT)
                    .where(condition).fetch()
                    .map(rec -> accountMapper.accountRecordToAccountDto(rec.into(ACCOUNT)))
                    .forEach(pageResponseDto::addToContent);
        }

        if (!pageResponseDto.getContent().isEmpty()) {
            return pageResponseDto;
        }

        if (accountSearchDto.getFirstName() != null && accountSearchDto.getLastName() != null) {
            String temp = accountSearchDto.getLastName();
            accountSearchDto.setLastName(null);
            findAccount(userId, accountSearchDto);
            accountSearchDto.setFirstName(null);
            accountSearchDto.setLastName(temp);
            findAccount(userId, accountSearchDto);
        } else {
            if (accountSearchDto.getIds() != null && !accountSearchDto.getIds().isEmpty()) {
                accountSearchDto.setAuthor(null);
            }
            Condition condition = (getConditionToAccountSearch(userId, accountSearchDto, true));
            dslContext.select().from(ACCOUNT)
                    .where(condition).fetch()
                    .map(rec -> accountMapper.accountRecordToAccountDto(rec.into(ACCOUNT)))
                    .forEach(pageResponseDto::addToContent);
            pageResponseDto.setTotalElements(pageResponseDto.getContent().size());
        }

        return pageResponseDto;
    }

    public PageResponseDto<AccountDto> findAccountByStatusCode(UUID userId, AccountSearchDto accountSearchDto) {
        PageResponseDto<AccountDto> pageAccountDto = new PageResponseDto<>();
        Condition condition = ACCOUNT.IS_DELETED.eq(accountSearchDto.isDeleted());

        condition = condition.and(getConditionToNames(userId, accountSearchDto.getFirstName(),
                accountSearchDto.getLastName(), accountSearchDto.getAuthor()));

        dslContext.select()
                .from(Account.ACCOUNT)
                .join(Friends.FRIENDS)
                .on(Friends.FRIENDS.REQUESTED_ACCOUNT_ID.eq(ACCOUNT.ID))
                .where(Friends.FRIENDS.ACCOUNT_FROM_ID.eq(userId))
                .and(Friends.FRIENDS.STATUS_CODE.eq(accountSearchDto.getStatusCode().toString()))
                .and(condition)
                .fetch().map(rec -> accountMapper.accountRecordToAccountDto(rec.into(ACCOUNT)))
                .forEach(pageAccountDto::addToContent);
        pageAccountDto.setTotalElements(pageAccountDto.getContent().size());

        return pageAccountDto;
    }

    private Condition getConditionToAccountSearch(UUID userId, AccountSearchDto accountSearchDto, Boolean isSearchInDialogs) {
        LocalDate maxBirthDate = accountSearchDto.getMaxBirthDate();
        LocalDate minBirthDate = accountSearchDto.getMinBirthDate();
        String firstName = accountSearchDto.getFirstName();
        String lastName = accountSearchDto.getLastName();
        String country = accountSearchDto.getCountry();
        String city = accountSearchDto.getCity();
        String author = accountSearchDto.getAuthor();
        List<String> ids = accountSearchDto.getIds();

        List<UUID> friendsIds = friendRepository.getFriendsIds(userId);
        List<UUID> friendshipRequestedIds = friendRepository.getFriendshipRequestedIds(userId);
        List<UUID> blockedIds = friendRepository.getBlockedAccountIds(userId);
        Condition condition = ACCOUNT.IS_DELETED.eq(accountSearchDto.isDeleted())
                .and(getConditionToNames(userId, firstName, lastName, author));
        if (!isSearchInDialogs) {
            condition.and(ACCOUNT.ID.notIn(friendsIds))
                    .and(ACCOUNT.ID.notIn(friendshipRequestedIds))
                    .and(ACCOUNT.ID.notIn(blockedIds));
        }

        if (ids != null && !ids.isEmpty()) {
            condition = condition.and(ACCOUNT.ID.in(ids));
        }
        if (country != null) {
            condition = condition.and(ACCOUNT.COUNTRY.equalIgnoreCase(country));
        }
        if (city != null) {
            condition = condition.and(ACCOUNT.CITY.equalIgnoreCase(city));
        }
        if (maxBirthDate != null) {
            condition = condition.and(ACCOUNT.BIRTH_DATE.le(maxBirthDate));
        }
        if (minBirthDate != null) {
            condition = condition.and(ACCOUNT.BIRTH_DATE.ge(minBirthDate));
        }

        return condition;
    }

    private Condition getConditionToNames(UUID userId, String firstName, String lastName, String author) {
        Condition condition = ACCOUNT.ID.ne(userId);
        char ch = '%';

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

        if (author != null) {
            condition = condition.and((ACCOUNT.FIRST_NAME.likeIgnoreCase(ch + author + ch))
                    .or(ACCOUNT.LAST_NAME.likeIgnoreCase(ch + author + ch)));
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

    private void updateNotificationType(AccountRecord accountRecord, NotificationTypeEnum notificationType, Boolean
            enable) {
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
