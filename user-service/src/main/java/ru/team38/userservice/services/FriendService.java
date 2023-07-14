package ru.team38.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.AccountDto;
import ru.team38.common.dto.FriendDto;
import ru.team38.common.dto.*;
import ru.team38.userservice.data.repositories.FriendRepository;
import ru.team38.userservice.exceptions.DatabaseQueryException;
import ru.team38.userservice.exceptions.FriendsServiceException;
import ru.team38.userservice.exceptions.status.UnauthorizedException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final AccountService accountService;

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
        if (friendSearchDto.getAgeFrom() != null) {
            friendSearchDto.setBirthDateFrom(LocalDate.now()
                    .minusYears(friendSearchDto.getAgeFrom()));
        }
        if (friendSearchDto.getAgeTo() != null) {
            friendSearchDto.setBirthDateTo(LocalDate.now()
                    .minusYears(friendSearchDto.getAgeTo()));
        }
        List<FriendShortDto> friendShortDtoList;
        try {
            friendShortDtoList = friendRepository.getFriendsByParameters(userId, friendSearchDto);
        } catch (DatabaseQueryException e) {
            log.error("Error executing getFriends request from account ID {}", userId, e);
            throw new FriendsServiceException("Error getting current user's friends", e);
        }
        if (friendShortDtoList.size() == 0) {
            throw new FriendsServiceException("No entries found", null);
        }
        return getPageFriendShortDto(friendShortDtoList, pageDto);
    }

    public PageFriendShortDto getPageFriendShortDto (List<FriendShortDto> friendShortDtoList, PageDto pageDto) {
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
}