package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.like.CreateLikeDto;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.gatewayservice.service.LikeService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class LikeController implements LikeControllerInterface {

    private final LikeService likeService;

    @Override
    @PostMapping("/{postId}/like")
    public LikeDto getLike(@RequestBody CreateLikeDto createLikeDto, @PathVariable UUID postId) {
        log.info("Executing getLike request");
        return likeService.getLikeByPost(createLikeDto, postId);
    }

    @Override
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<String> deleteLike(@PathVariable UUID postId) {
        log.info("Executing deleteLike request");
        return likeService.deleteLikeByPost(postId);
    }

    @Override
    @PostMapping("/{postId}/comment/{commentId}/like")
    public LikeDto getLikeByComment(@PathVariable UUID postId, @PathVariable UUID commentId) {
        log.info("Executing getLike request");
        return likeService.getLikeByComment(postId, commentId);
    }

    @Override
    @DeleteMapping("/{postId}/comment/{commentId}/like")
    public ResponseEntity<String> deleteLikeByComment(@PathVariable UUID postId, @PathVariable UUID commentId) {
        log.info("Executing deleteLike request");
        return likeService.deleteLikeByComment(postId, commentId);
    }
}
