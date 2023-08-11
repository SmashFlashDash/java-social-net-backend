package ru.team38.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.team38.common.dto.like.CreateLikeDto;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.gatewayservice.clients.CommunicationsServiceClient;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final CommunicationsServiceClient communicationsServiceClient;

    public LikeDto getLikeByPost(@RequestBody CreateLikeDto createLikeDto, UUID postId) {
        ResponseEntity<LikeDto> responseEntity = communicationsServiceClient.getLikeByPost(createLikeDto, postId);
        return responseEntity.getBody();
    }

    public ResponseEntity<String> deleteLikeByPost(UUID postId) {
        return communicationsServiceClient.deleteLikeByPost(postId);
    }

    public LikeDto getLikeByComment(UUID postId, UUID commentId) {
        ResponseEntity<LikeDto> responseEntity = communicationsServiceClient.getLikeByComment(postId, commentId);
        return responseEntity.getBody();
    }

    public ResponseEntity<String> deleteLikeByComment(UUID postId, UUID commentId) {
        return communicationsServiceClient.deleteLikeByComment(postId, commentId);
    }

}
