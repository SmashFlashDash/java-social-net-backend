package ru.team38.communicationsservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.like.CreateLikeDto;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.communicationsservice.services.LikeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeDto> getLikeByPost(HttpServletRequest request,
                                                 @RequestBody CreateLikeDto createLikeDto,
                                                 @PathVariable UUID postId) {
        return ResponseEntity.ok(likeService.getPostLike(request, createLikeDto, postId));
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<String> deleteLikeByPost(HttpServletRequest request, @PathVariable UUID postId) {
        likeService.deleteLike(request, postId);
        return ResponseEntity.ok("Like удален");
    }

    @PostMapping("/{postId}/comment/{commentId}/like")
    public ResponseEntity<LikeDto> getLikeByComment(HttpServletRequest request,
                                                    @PathVariable UUID postId,
                                                    @PathVariable UUID commentId) {
        return ResponseEntity.ok(likeService.getCommentLike(request, commentId));
    }

    @DeleteMapping("/{postId}/comment/{commentId}/like")
    public ResponseEntity<String> deleteLikeByComment(HttpServletRequest request,
                                                      @PathVariable UUID postId,
                                                      @PathVariable UUID commentId) {
        likeService.deleteLike(request, commentId);
        return ResponseEntity.ok("Like удален");
    }
}
