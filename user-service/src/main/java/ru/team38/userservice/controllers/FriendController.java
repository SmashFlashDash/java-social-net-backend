package ru.team38.userservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.*;
import ru.team38.userservice.exceptions.FriendsServiceException;
import ru.team38.userservice.services.FriendService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/count")
    public ResponseEntity<CountDto> getIncomingFriendRequestsCount() throws FriendsServiceException {
        return ResponseEntity.ok(friendService.getIncomingFriendRequestsCount());
    }

    @GetMapping("")
    public ResponseEntity<PageFriendShortDto> getFriendsByParameters(FriendSearchDto friendSearchDto, PageDto pageDto) {
        return ResponseEntity.ok(friendService.getFriendsByParameters(friendSearchDto, pageDto));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<FriendShortDto>> getFriendsRecommendations(FriendSearchDto friendSearchDto) {
        return ResponseEntity.ok(friendService.getFriendsRecommendations(friendSearchDto));
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<FriendShortDto> blockAccount(HttpServletRequest request, @PathVariable UUID id) {
        return ResponseEntity.ok(friendService.blockAccount(request, id));
    }

    @PutMapping("/unblock/{id}")
    public ResponseEntity<FriendShortDto> unblockAccount(HttpServletRequest request, @PathVariable UUID id) {
        return ResponseEntity.ok(friendService.unblockAccount(request, id));
    }
}