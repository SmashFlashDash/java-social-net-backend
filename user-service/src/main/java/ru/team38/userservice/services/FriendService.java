package ru.team38.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.account.AccountDto;
import ru.team38.common.dto.friend.FriendSearchDto;
import ru.team38.common.dto.friend.FriendShortDto;
import ru.team38.common.dto.notification.NotificationTypeEnum;
import ru.team38.common.dto.other.*;
import ru.team38.common.jooq.tables.records.FriendsRecord;
import ru.team38.common.services.NotificationAddService;
import ru.team38.userservice.data.repositories.FriendRepository;
import ru.team38.userservice.exceptions.DatabaseQueryException;
import ru.team38.userservice.exceptions.FriendsServiceException;
import ru.team38.userservice.exceptions.status.BadRequestException;
import ru.team38.userservice.exceptions.status.UnauthorizedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.team38.common.jooq.Tables.ACCOUNT;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final AccountService accountService;
    private final NotificationAddService notificationAddService;
    @Value("${preferences.friendship-recommendations.age-limit-bottom:5}")
    private int ageLimitBottom;
    @Value("${preferences.friendship-recommendations.age-limit-top:5}")
    private int ageLimitTop;

    public CountDto getIncomingFriendRequestsCount() throws FriendsServiceException {
        log.info("Executing getIncomingFriendRequests request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        UUID userId = accountService.getAuthenticatedAccount().getId();
        try {
            return new CountDto(friendRepository.countIncomingFriendRequests(userId));
        } catch (DatabaseQueryException e) {
            log.error("Error executing getIncomingFriendRequestsCount request from account ID {}", userId, e);
            throw new FriendsServiceException("Error counting incoming friend requests", e);
        }
    }

    public PageResponseDto<Object> getFriendsByParameters(FriendSearchDto friendSearchDto, PageDto pageDto) {
        log.info("Executing getFriends request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        UUID userId = accountService.getAuthenticatedAccount().getId();
        friendSearchDto = ageToBirthDate(friendSearchDto);
        friendSearchDto.setId(userId);
        Condition condition = getCondition(friendSearchDto);
        StatusCode statusCode = friendSearchDto.getStatusCode();
        List<Object> friendsList;
        try {
            if (pageDto.getSize() == null) {
                friendsList = friendRepository.getFriendsByParameters(userId, condition, statusCode);
            } else {
                friendsList = friendRepository.getFriendsByParametersTabs(userId, condition, statusCode);
            }
        } catch (DatabaseQueryException e) {
            log.error("Error executing getFriends request from account ID {}", userId, e);
            throw new FriendsServiceException("Error getting current user's friends", e);
        }
        if (friendsList.size() == 0) {
            return getPageFriendShortDto(new ArrayList<>(), pageDto);
        }
        return getPageFriendShortDto(friendsList, pageDto);
    }

    public PageResponseDto<Object> getPageFriendShortDto(List<Object> friendsList, PageDto pageDto) {
        SortDto sort = new SortDto(true, false, true);

        return PageResponseDto.builder()
                .totalElements(friendsList.size())
                .totalPages(1)
                .number(0)
                .size(pageDto.getSize())
                .content(friendsList)
                .sort(sort)
                .first(true)
                .last(true)
                .numberOfElements(friendsList.size())
                .empty(false)
                .build();
    }

    public List<FriendShortDto> getFriendsRecommendations(FriendSearchDto friendSearchDto) {
        log.info("Executing getFriendsRecommendations request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        UUID userId = accountService.getAuthenticatedAccount().getId();
        List<UUID> friendsIds = friendRepository.getFriendsIds(userId);
        List<UUID> friendsFriendsIds = getFriendsFriendsIds(userId, friendsIds);
        List<UUID> finalIds = new ArrayList<>(friendsFriendsIds);
        finalIds.addAll(getRecommendationsIds(friendSearchDto, friendsIds, friendsFriendsIds));
        List<FriendShortDto> finalRecommendations = new ArrayList<>();
        if (!finalIds.isEmpty()) {
            finalRecommendations = friendRepository.getFinalRecommendations(finalIds);
        }
        return finalRecommendations;
    }

    public List<UUID> getFriendsFriendsIds(UUID userId, List<UUID> friendsIds) {
        List<UUID> friendsFriendsIds;
        if (!friendsIds.isEmpty()) {
            friendsFriendsIds = friendRepository.getFriendsFriendsIds(userId, friendsIds);
        } else {
            friendsFriendsIds = new ArrayList<>();
        }
        return friendsFriendsIds;
    }

    public List<UUID> getRecommendationsIds(FriendSearchDto friendSearchDto, List<UUID> friendsIds, List<UUID> friendsFriendsIds) {
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        UUID accountId = accountDto.getId();
        if (friendSearchDto.allNull()) {
            friendSearchDto.setCity(accountDto.getCity());
            friendSearchDto.setBirthDateFrom(accountDto.getBirthDate().minusYears(ageLimitBottom));
            friendSearchDto.setBirthDateTo(accountDto.getBirthDate().plusYears(ageLimitTop));
        } else {
            friendSearchDto = ageToBirthDate(friendSearchDto);
        }
        friendSearchDto.setId(accountId);
        Condition condition = getCondition(friendSearchDto);
        List<UUID> recommendationsIds;
        try {
            recommendationsIds = friendRepository.getRecommendationsIds(accountId, friendsIds, friendsFriendsIds, condition);
        } catch (DatabaseQueryException e) {
            log.error("Error executing getRecommendationsIds request from account ID {}", accountDto.getId(), e);
            throw new FriendsServiceException("Error getting friendship recommendation IDs", e);
        }
        return recommendationsIds;
    }

    public Condition getCondition(FriendSearchDto friendSearchDto) {
        Condition condition = ACCOUNT.ID.ne(friendSearchDto.getId());
        if (friendSearchDto.getFirstName() != null) {
            condition = condition.and(ACCOUNT.FIRST_NAME.eq(friendSearchDto.getFirstName()));
        }
        if (friendSearchDto.getCity() != null) {
            condition = condition.and(ACCOUNT.CITY.eq(friendSearchDto.getCity()));
        }
        if (friendSearchDto.getCountry() != null) {
            condition = condition.and(ACCOUNT.COUNTRY.eq(friendSearchDto.getCountry()));
        }
        if (friendSearchDto.getBirthDateFrom() != null) {
            condition = condition.and(ACCOUNT.BIRTH_DATE.ge(friendSearchDto.getBirthDateFrom()));
        }
        if (friendSearchDto.getBirthDateTo() != null) {
            condition = condition.and(ACCOUNT.BIRTH_DATE.le(friendSearchDto.getBirthDateTo()));
        }
        return condition;
    }

    public FriendSearchDto ageToBirthDate(FriendSearchDto friendSearchDto) {
        if (friendSearchDto.getAgeFrom() != null) {
            friendSearchDto.setBirthDateTo(LocalDate.now().minusYears(friendSearchDto.getAgeFrom()));
        }
        if (friendSearchDto.getAgeTo() != null) {
            friendSearchDto.setBirthDateFrom(LocalDate.now().minusYears(friendSearchDto.getAgeTo()));
        }
        return friendSearchDto;
    }

    @LoggingMethod
    @Transactional
    public FriendShortDto blockAccount(UUID accountToBlockId) {
        UUID initiatorId = accountService.getAuthenticatedAccount().getId();
        FriendShortDto resultDto;

        FriendsRecord requestedFriendRecord = friendRepository.getFriendRecordByIds(accountToBlockId, initiatorId);
        if (requestedFriendRecord != null) {
            if (!requestedFriendRecord.getStatusCode().equals(StatusCode.BLOCKED.name())) {
                friendRepository.updateRecord(accountToBlockId, initiatorId, StatusCode.NONE);
            }
        } else {
            friendRepository.insertRecord(accountToBlockId, initiatorId, StatusCode.NONE);
        }
        FriendsRecord initiatorFriendRecord = friendRepository.getFriendRecordByIds(initiatorId, accountToBlockId);
        if (initiatorFriendRecord != null) {
            if (initiatorFriendRecord.getStatusCode().equals(StatusCode.BLOCKED.name())) {
                throw new BadRequestException("User is already blocked " + accountToBlockId);
            }
            resultDto = friendRepository.updateRecord(initiatorId, accountToBlockId, StatusCode.BLOCKED);
        } else {
            resultDto = friendRepository.insertRecord(initiatorId, accountToBlockId, StatusCode.BLOCKED);
        }
        resultDto.setId(resultDto.getFriendId());
        return resultDto;
    }

    @LoggingMethod
    @Transactional
    public FriendShortDto unblockAccount(UUID accountToUnblockId) {
        UUID initiatorId = accountService.getAuthenticatedAccount().getId();
        FriendShortDto resultDto;

        FriendsRecord friendRecord = friendRepository.getFriendRecordByIds(initiatorId, accountToUnblockId);
        if (friendRecord != null) {
            if (!friendRecord.getStatusCode().equals(StatusCode.BLOCKED.name())) {
                throw new BadRequestException("The specified account is not blocked " + accountToUnblockId);
            }
            resultDto = friendRepository.updateRecord(initiatorId, accountToUnblockId, StatusCode.NONE);
        } else {
            throw new BadRequestException("The specified account is not blocked or does not exist " + accountToUnblockId);
        }
        resultDto.setId(resultDto.getFriendId());
        return resultDto;
    }

    @LoggingMethod
    @Transactional
    public FriendShortDto makeFriendRequest(UUID friendRequestId) {
        UUID initiatorId = accountService.getAuthenticatedAccount().getId();
        FriendShortDto resultDto;

        FriendsRecord requestedFriendRecord = friendRepository.getFriendRecordByIds(friendRequestId, initiatorId);
        if (requestedFriendRecord != null) {
            if (requestedFriendRecord.getStatusCode().equals(StatusCode.REQUEST_FROM.name())) {
                throw new BadRequestException("Friend request for user has already been made " + friendRequestId);
            }
            if (requestedFriendRecord.getStatusCode().equals(StatusCode.FRIEND.name())) {
                throw new BadRequestException("Users are already friends " + initiatorId + ", " + friendRequestId);
            }
            if (requestedFriendRecord.getStatusCode().equals(StatusCode.BLOCKED.name())) {
                throw new BadRequestException("Impossible to make friend request to user that blocked current user " + friendRequestId);
            }
            friendRepository.updateRecord(friendRequestId, initiatorId, StatusCode.REQUEST_FROM);
        } else {
            friendRepository.insertRecord(friendRequestId, initiatorId, StatusCode.REQUEST_FROM);
        }
        FriendsRecord initiatorFriendRecord = friendRepository.getFriendRecordByIds(initiatorId, friendRequestId);
        if (initiatorFriendRecord != null) {
            if (initiatorFriendRecord.getStatusCode().equals(StatusCode.REQUEST_TO.name())) {
                throw new BadRequestException("Friend request for user has already been made " + friendRequestId);
            }
            if (initiatorFriendRecord.getStatusCode().equals(StatusCode.FRIEND.name())) {
                throw new BadRequestException("Users are already friends " + initiatorId + ", " + friendRequestId);
            }
            resultDto = friendRepository.updateRecord(initiatorId, friendRequestId, StatusCode.REQUEST_TO);
        } else {
            resultDto = friendRepository.insertRecord(initiatorId, friendRequestId, StatusCode.REQUEST_TO);
        }
        resultDto.setId(resultDto.getFriendId());
        notificationAddService.addNotification(initiatorId, resultDto, NotificationTypeEnum.FRIEND_REQUEST);
        return resultDto;
    }

    @LoggingMethod
    @Transactional
    public FriendShortDto approveFriendRequest(UUID friendApproveId) {
        UUID initiatorId = accountService.getAuthenticatedAccount().getId();
        FriendShortDto resultDto;

        FriendsRecord requestedFriendRecord = friendRepository.getFriendRecordByIds(friendApproveId, initiatorId);
        if (requestedFriendRecord != null) {
            if (requestedFriendRecord.getStatusCode().equals(StatusCode.FRIEND.name())) {
                throw new BadRequestException("Users are already friends " + initiatorId + ", " + friendApproveId);
            }
            friendRepository.updateRecord(friendApproveId, initiatorId, StatusCode.FRIEND);
        } else {
            throw new BadRequestException("No entry in the 'Friends' table for users " + initiatorId + ", " + friendApproveId);
        }
        FriendsRecord initiatorFriendRecord = friendRepository.getFriendRecordByIds(initiatorId, friendApproveId);
        if (initiatorFriendRecord != null) {
            if (initiatorFriendRecord.getStatusCode().equals(StatusCode.FRIEND.name())) {
                throw new BadRequestException("Users are already friends " + initiatorId + ", " + friendApproveId);
            }
            resultDto = friendRepository.updateRecord(initiatorId, friendApproveId, StatusCode.FRIEND);
        } else {
            throw new BadRequestException("No entry in the 'Friends' table for users " + initiatorId + ", " + friendApproveId);
        }
        resultDto.setId(resultDto.getFriendId());
        notificationAddService.addNotification(initiatorId, resultDto, NotificationTypeEnum.FRIEND_REQUEST);
        return resultDto;
    }

    @LoggingMethod
    @Transactional
    public void deleteRelationship(UUID deleteId) {
        UUID initiatorId = accountService.getAuthenticatedAccount().getId();

        FriendsRecord requestedFriendRecord = friendRepository.getFriendRecordByIds(deleteId, initiatorId);
        if (requestedFriendRecord != null) {
            if (requestedFriendRecord.getStatusCode().equals(StatusCode.NONE.name())) {
                throw new BadRequestException("Relationship between users doesn't exist " + initiatorId + ", " + deleteId);
            }
            friendRepository.updateRecord(deleteId, initiatorId, StatusCode.NONE);
        } else {
            throw new BadRequestException("No entry in the 'Friends' table for users " + initiatorId + ", " + deleteId);
        }
        FriendsRecord initiatorFriendRecord = friendRepository.getFriendRecordByIds(initiatorId, deleteId);
        if (initiatorFriendRecord != null) {
            if (initiatorFriendRecord.getStatusCode().equals(StatusCode.NONE.name())) {
                throw new BadRequestException("Relationship between users doesn't exist " + initiatorId + ", " + deleteId);
            }
            friendRepository.updateRecord(initiatorId, deleteId, StatusCode.NONE);
        } else {
            throw new BadRequestException("No entry in the 'Friends' table for users " + initiatorId + ", " + deleteId);
        }
    }

    @LoggingMethod
    @Transactional
    public FriendShortDto getSubscription(UUID subscribedId) {
        UUID initiatorId = accountService.getAuthenticatedAccount().getId();
        FriendShortDto resultDto;

        FriendsRecord requestedFriendRecord = friendRepository.getFriendRecordByIds(subscribedId, initiatorId);
        if (requestedFriendRecord != null) {
            if (requestedFriendRecord.getStatusCode().equals(StatusCode.SUBSCRIBED.name())) {
                throw new BadRequestException("Subscription already exists " + initiatorId + ", " + subscribedId);
            }
            if (requestedFriendRecord.getStatusCode().equals(StatusCode.BLOCKED.name())) {
                throw new BadRequestException("Impossible to subscribe to user that blocked current user " + subscribedId);
            }
            friendRepository.updateRecord(subscribedId, initiatorId, StatusCode.SUBSCRIBED);
        } else {
            friendRepository.insertRecord(subscribedId, initiatorId, StatusCode.SUBSCRIBED);
        }
        FriendsRecord initiatorFriendRecord = friendRepository.getFriendRecordByIds(initiatorId, subscribedId);
        if (initiatorFriendRecord != null) {
            if (initiatorFriendRecord.getStatusCode().equals(StatusCode.WATCHING.name())) {
                throw new BadRequestException("Subscription already exists " + initiatorId + ", " + subscribedId);
            }
            resultDto = friendRepository.updateRecord(initiatorId, subscribedId, StatusCode.WATCHING);
        } else {
            resultDto = friendRepository.insertRecord(initiatorId, subscribedId, StatusCode.WATCHING);
        }
        resultDto.setId(resultDto.getFriendId());
        return resultDto;
    }
}