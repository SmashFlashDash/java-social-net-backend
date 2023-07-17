package ru.team38.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.AccountDto;
import ru.team38.common.dto.FriendDto;
import ru.team38.common.dto.*;
import ru.team38.userservice.data.repositories.FriendRepository;
import ru.team38.userservice.exceptions.DatabaseQueryException;
import ru.team38.userservice.exceptions.FriendsServiceException;
import ru.team38.userservice.exceptions.status.UnauthorizedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.team38.common.jooq.Tables.ACCOUNT;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final AccountService accountService;

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

    public Integer getIncomingFriendRequestsCount() throws FriendsServiceException {
        log.info("Executing getIncomingFriendRequests request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        UUID userId = accountService.getAuthenticatedAccount().getId();
        try {
            return friendRepository.countIncomingFriendRequests(userId);
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
        List<FriendShortDto> friendShortDtoList;
        try {
            friendShortDtoList = friendRepository.getFriendsByParameters(userId, condition);
        } catch (DatabaseQueryException e) {
            log.error("Error executing getFriends request from account ID {}", userId, e);
            throw new FriendsServiceException("Error getting current user's friends", e);
        }
        if (friendShortDtoList.size() == 0) {
            throw new FriendsServiceException("No entries found", null);
        }
        return getPageFriendShortDto(friendShortDtoList, pageDto);
    }

    public PageFriendShortDto getPageFriendShortDto(List<FriendShortDto> friendShortDtoList, PageDto pageDto) {
        Sort sort = new Sort(
                true,
                true,
                true
        );
        PageableObject pageableObject = new PageableObject(
                0,
                sort,
                pageDto.getSize(),
                true,
                true,
                pageDto.getPage()
        );
        return new PageFriendShortDto(
                friendShortDtoList.size(),
                0,
                pageDto.getPage(),
                pageDto.getSize(),
                friendShortDtoList,
                sort,
                true,
                true,
                0,
                pageableObject,
                true
        );
    }

    public List<FriendShortDto> getFriendsRecommendations(FriendSearchDto friendSearchDto) {
        log.info("Executing getFriendsRecommendations request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        UUID userId = accountService.getAuthenticatedAccount().getId();
        List<UUID> finalIds = new ArrayList<>();
        List<UUID> friendsIds = getFriendsIds(userId);
        List<UUID> friendsFriendsIds = new ArrayList<>();
        if (friendsIds.size() != 0) {
            friendsFriendsIds = getFriendsFriendsIds(userId, friendsIds);
            finalIds.addAll(friendsFriendsIds);
        }
        finalIds.addAll(getRecommendationsIds(friendSearchDto, friendsIds, friendsFriendsIds));
        List<FriendShortDto> finalRecommendations = new ArrayList<>();
        if (finalIds.size() != 0) {
            try {
                finalRecommendations = friendRepository.getFinalRecommendations(userId, finalIds);
            } catch (DatabaseQueryException e) {
                log.error("Error executing getFinalRecommendations request from account ID {}", userId, e);
                throw new FriendsServiceException("Error getting list of accounts for friendship recommendations", e);
            }
        }
        return finalRecommendations;
    }

    public List<UUID> getFriendsIds(UUID userId) {
        List<UUID> friendsIds;
        try {
            friendsIds = friendRepository.getFriendsIds(userId, null);
        } catch (DatabaseQueryException e) {
            log.error("Error executing getFriendsIds request from account ID {}", userId, e);
            throw new FriendsServiceException("Error getting current user's friends' IDs", e);
        }
        return friendsIds;
    }

    public List<UUID> getFriendsFriendsIds(UUID userId, List<UUID> friendsIds) {
        List<UUID> friendsFriendsIds;
        String friendsIdsString = friendsIds.stream()
                .map(String::valueOf)
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(", "));
            try {
                friendsFriendsIds = friendRepository.getFriendsIds(userId, friendsIdsString);
            } catch (DatabaseQueryException e) {
                log.error("Error executing getFriendsIds request from account ID {}", userId, e);
                throw new FriendsServiceException("Error getting current user's friends' IDs", e);
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
}