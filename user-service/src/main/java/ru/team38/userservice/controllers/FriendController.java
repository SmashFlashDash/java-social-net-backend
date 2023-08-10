package ru.team38.userservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.friend.FriendSearchDto;
import ru.team38.common.dto.friend.FriendShortDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.common.dto.other.PageDto;
import ru.team38.common.dto.other.PageResponseDto;
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
    public ResponseEntity<PageResponseDto<Object>> getFriendsByParameters(FriendSearchDto friendSearchDto, PageDto pageDto) {
        return ResponseEntity.ok(friendService.getFriendsByParameters(friendSearchDto, pageDto));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<FriendShortDto>> getFriendsRecommendations(FriendSearchDto friendSearchDto) {
        return ResponseEntity.ok(friendService.getFriendsRecommendations(friendSearchDto));
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<FriendShortDto> blockAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(friendService.blockAccount(id));
    }

    @PutMapping("/unblock/{id}")
    public ResponseEntity<FriendShortDto> unblockAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(friendService.unblockAccount(id));
    }

    @PostMapping("/{id}/request")
    public ResponseEntity<FriendShortDto> makeFriendRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(friendService.makeFriendRequest(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<FriendShortDto> approveFriendRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(friendService.approveFriendRequest(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelationship(@PathVariable UUID id) {
        friendService.deleteRelationship(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/subscribe/{id}")
    public ResponseEntity<FriendShortDto> getSubscription(@PathVariable UUID id) {
        return ResponseEntity.ok(friendService.getSubscription(id));
    }

}