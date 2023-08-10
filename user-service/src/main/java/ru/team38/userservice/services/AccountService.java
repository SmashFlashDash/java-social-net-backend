package ru.team38.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.account.AccountDto;
import ru.team38.common.dto.account.AccountSearchDto;
import ru.team38.common.dto.other.PageDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.dto.other.SortDto;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.mappers.AccountMapper;
import ru.team38.userservice.data.repositories.AccountRepository;
import ru.team38.userservice.data.repositories.GeoRepository;
import ru.team38.userservice.exceptions.status.BadRequestException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final DSLContext DSL;
    private final Account ACCOUNT = Account.ACCOUNT;
    private final AccountMapper mapper = Mappers.getMapper(AccountMapper.class);
    private final AccountRepository accountRepository;
    private final GeoRepository geoRepository;

    @LoggingMethod
    public AccountDto getAuthenticatedAccount() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepository.getAccountByEmail(name)
                .orElseThrow(() -> new BadRequestException("User not found"))
                .map(rec -> mapper.accountRecordToAccountDto((AccountRecord) rec));
    }

    @LoggingMethod
    public AccountDto getAuthenticatedAccountWithUpdateOnline() {
        AccountDto account = getAuthenticatedAccount();
        account.setLastOnlineTime(ZonedDateTime.now());
        account.setIsOnline(true);
        accountRepository.updateAccount(account);
        return account;
    }

    @LoggingMethod
    public AccountDto createAccount(AccountDto accountDto) {
        return accountRepository.save(accountDto);
    }

    @LoggingMethod
    @SneakyThrows
    public AccountDto updateAccount(AccountDto accountDto) {
        AccountDto updateDto = getAuthenticatedAccount();
        String city = accountDto.getCity();
        String country = accountDto.getCountry();
        for (Field field : AccountDto.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.get(accountDto) != null) {
                field.set(updateDto, field.get(accountDto));
            }
        }
        updateDto.setUpdatedOn(ZonedDateTime.now());
        if (city != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                geoRepository.addZoneIdInCitiesTable(city, country);
            });
            executor.shutdown();
        }
        return accountRepository.updateAccount(updateDto);
    }

    @LoggingMethod
    public void deleteAccount() {
        AccountDto account = getAuthenticatedAccount();
        account.setIsDeleted(true);
        accountRepository.updateAccount(account);
    }

    @LoggingMethod
    public AccountDto getAccountById(UUID id) {
        AccountRecord accountRecord = DSL.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id))
                .fetchOne();
        return mapper.accountRecordToAccountDto(accountRecord);
    }

    @LoggingMethod
    public PageResponseDto<AccountDto> findAccount(AccountSearchDto accountSearchDto, PageDto pageDto) {
        UUID userId = getAuthenticatedAccount().getId();
        return getPageAccountDto(accountRepository
                .findAccount(userId, checkDataToFindAccount(accountSearchDto)), pageDto);
    }

    @LoggingMethod
    public PageResponseDto<AccountDto> findAccountByStatusCode(AccountSearchDto accountSearchDto, PageDto pageDto) {
        UUID userId = getAuthenticatedAccount().getId();
        return getPageAccountDto(accountRepository
                .findAccountByStatusCode(userId, checkDataToFindAccount(accountSearchDto)), pageDto);
    }

    public PageResponseDto<AccountDto> getPageAccountDto(PageResponseDto<AccountDto> pageAccountDto, PageDto pageDto) {
        SortDto sort = new SortDto(true, false, true);
        pageAccountDto.setSort(sort);
        pageAccountDto.setTotalElements(pageAccountDto.getContent().size());
        pageAccountDto.setFirst(true);
        pageAccountDto.setLast(true);
        pageAccountDto.setEmpty(false);
        return pageAccountDto;
    }

    private AccountSearchDto checkDataToFindAccount(AccountSearchDto accountSearchDto) {

        if (accountSearchDto.getAuthor() != null) {
            accountSearchDto.setFirstName(accountSearchDto.getAuthor());
        }

        if (accountSearchDto.getFirstName() != null) {
            accountSearchDto.setFirstName(accountSearchDto.getFirstName().trim());
            String[] firstName = accountSearchDto.getFirstName().split(" ");
            if (firstName.length > 1 && accountSearchDto.getLastName() == null) {
                accountSearchDto.setFirstName(firstName[0]);
                accountSearchDto.setLastName(firstName[firstName.length - 1]);
            }
            if (firstName.length > 1) {
                accountSearchDto.setFirstName(firstName[0]);
            }
        }
        if (accountSearchDto.getLastName() != null) {
            accountSearchDto.setLastName(accountSearchDto.getLastName().trim());
            String[] lastName = accountSearchDto.getLastName().split(" ");
            if (lastName.length > 1 && accountSearchDto.getFirstName() == null) {
                accountSearchDto.setFirstName(lastName[0]);
                accountSearchDto.setLastName(lastName[lastName.length - 1]);
            }
            if (lastName.length > 1) {
                accountSearchDto.setFirstName(lastName[0]);
            }
        }
        return checkAgeToFindAccount(accountSearchDto);
    }

    private AccountSearchDto checkAgeToFindAccount(AccountSearchDto accountSearchDto) {
        if (accountSearchDto.getAgeFrom() != null) {
            accountSearchDto.setMaxBirthDate(LocalDate.now()
                    .minusYears(accountSearchDto.getAgeFrom()));
        }
        if (accountSearchDto.getAgeTo() != null) {
            accountSearchDto.setMinBirthDate(LocalDate.now()
                    .minusYears(accountSearchDto.getAgeTo()));
        }
        return accountSearchDto;
    }
}