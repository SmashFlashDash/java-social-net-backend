package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.comment.*;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.gatewayservice.service.CommentService;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/v1/post/{postId}/comment")
    ResponseEntity<CommentDto> createComment(@PathVariable UUID postId,
                                             @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(commentService.createComment(postId, payload));
    }

    @PutMapping("/api/v1/post/{postId}/comment")
    ResponseEntity<CommentDto> updateComment(@PathVariable UUID postId,
                                             @RequestBody CommentUpdateDto commentUpdateDto) {
        return ResponseEntity.ok(commentService.updateComment(postId, commentUpdateDto));
    }

    @DeleteMapping("/api/v1/post/{postId}/comment/{commentId}")
    ResponseEntity<String> deleteComment(@PathVariable UUID postId,
                                         @PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.deleteComment(postId, commentId));
    }

    @GetMapping("/api/v1/post/{postId}/comment")
    ResponseEntity<PageResponseDto<CommentDto>> getComments(@PathVariable UUID postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getComments(postId, pageable));
    }

    @GetMapping("/api/v1/post/{postId}/comment/{commentId}/subcomment")
    ResponseEntity<PageResponseDto<CommentDto>> getSubComments(@PathVariable UUID postId,
                                                    @PathVariable UUID commentId,
                                                    Pageable pageable) {
        return ResponseEntity.ok(commentService.getSubComments(postId, commentId, pageable));
    }
}
