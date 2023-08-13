package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.dto.comment.CommentUpdateDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.gatewayservice.service.CommentService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentControllerInterface {

    private final CommentService commentService;

    @Override
    @PostMapping("/api/v1/post/{postId}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable UUID postId,
                                             @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(commentService.createComment(postId, payload));
    }

    @Override
    @PutMapping("/api/v1/post/{postId}/comment")
    public ResponseEntity<CommentDto> updateComment(@PathVariable UUID postId,
                                             @RequestBody CommentUpdateDto commentUpdateDto) {
        return ResponseEntity.ok(commentService.updateComment(postId, commentUpdateDto));
    }

    @Override
    @DeleteMapping("/api/v1/post/{postId}/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable UUID postId,
                                         @PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.deleteComment(postId, commentId));
    }

    @Override
    @GetMapping("/api/v1/post/{postId}/comment")
    public ResponseEntity<PageResponseDto<CommentDto>> getComments(@PathVariable UUID postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getComments(postId, pageable));
    }

    @Override
    @GetMapping("/api/v1/post/{postId}/comment/{commentId}/subcomment")
    public ResponseEntity<PageResponseDto<CommentDto>> getSubComments(@PathVariable UUID postId,
                                                               @PathVariable UUID commentId,
                                                               Pageable pageable) {
        return ResponseEntity.ok(commentService.getSubComments(postId, commentId, pageable));
    }
}
