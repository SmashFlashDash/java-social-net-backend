package ru.team38.userservice.services;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.team38.common.dto.*;
import ru.team38.userservice.data.repositories.FriendRepository;
import ru.team38.userservice.exceptions.DatabaseQueryException;
import ru.team38.userservice.exceptions.FriendsServiceException;
import ru.team38.userservice.exceptions.status.UnauthorizedException;
import ru.team38.userservice.security.jwt.JwtService;

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
    private final JwtService jwtService;

    @Value("${preferences.friendship-recommendations.age-limit-bottom:5}")
    private int ageLimitBottom;
    @Value("${preferences.friendship-recommendations.age-limit-top:5}")
    private int ageLimitTop;

    public List<FriendDto> getIncomingFriendRequests() throws FriendsServiceException {
        log.info("Executing getIncomingFriendRequests request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        AccountDto accountDto = accountService.getAuthenticatedAccount();
        UUID userId = accountDto.getId();
        try {
            return friendRepository.getIncomingFriendRequests(userId);
        } catch (DatabaseQueryException e) {
            log.error("Error executing getIncomingFriendRequests request from account ID {}", userId, e);
            throw new FriendsServiceException("Error getting incoming friend requests", e);
        }
    }

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

    public PageFriendShortDto getFriendsByParameters(FriendSearchDto friendSearchDto, PageDto pageDto) {
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

    public PageFriendShortDto getPageFriendShortDto(List<Object> friendsList, PageDto pageDto) {
        Sort sort = new Sort(
                true,
                false,
                true
        );
        PageableObject pageableObject = new PageableObject(
                0,
                sort,
                pageDto.getSize(),
                true,
                false,
                0
        );
        return new PageFriendShortDto(
                friendsList.size(),
                1,
                0,
                pageDto.getSize(),
                friendsList,
                sort,
                true,
                true,
                friendsList.size(),
                pageableObject,
                false
        );
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
        if (friendSearchDto.allNull()) {
            friendSearchDto.setCity(accountDto.getCity());
            friendSearchDto.setBirthDateFrom(accountDto.getBirthDate().minusYears(ageLimitBottom));
            friendSearchDto.setBirthDateTo(accountDto.getBirthDate().plusYears(ageLimitTop));
        } else {
            friendSearchDto = ageToBirthDate(friendSearchDto);
        }
        friendSearchDto.setId(accountDto.getId());
        Condition condition = getCondition(friendSearchDto);
        List<UUID> recommendationsIds;
        try {
            recommendationsIds = friendRepository.getRecommendationsIds(friendsIds, friendsFriendsIds, condition);
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
    public FriendShortDto blockAccount(HttpServletRequest request, UUID accountToBlockId) {
        String initiatorUsername = getInitiatorUsername(request);
        return friendRepository.blockAccount(initiatorUsername, accountToBlockId);
    }

    @LoggingMethod
    @Transactional
    public FriendShortDto unblockAccount(HttpServletRequest request, UUID accountToUnblockId) {
        String initiatorUsername = getInitiatorUsername(request);
        return friendRepository.unblockAccount(initiatorUsername, accountToUnblockId);
    }

    private String getInitiatorUsername(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return jwtService.getUsername(bearerToken.substring(7));
    }
}