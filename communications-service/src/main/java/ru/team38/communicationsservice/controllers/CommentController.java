package ru.team38.communicationsservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.dto.comment.CommentUpdateDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.communicationsservice.services.CommentService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentDto> createCommentOrSubComment(HttpServletRequest request, @PathVariable UUID postId,
                                                                @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(commentService.createComment(request, postId, payload));
    }

    @PutMapping("/{postId}/comment")
    public ResponseEntity<CommentDto> updateComment(HttpServletRequest request,
                                                    @PathVariable UUID postId,
                                                    @RequestBody CommentUpdateDto commentUpdateDto) {
        return ResponseEntity.ok(commentService.updateComment(request, commentUpdateDto));
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<String> deleteComment(HttpServletRequest request,
                                                @PathVariable UUID postId,
                                                @PathVariable UUID commentId) {
        commentService.deleteComment(request, postId, commentId);
        return ResponseEntity.ok("Комментарий удален");
    }

    @GetMapping("/{postId}/comment")
    public ResponseEntity<PageResponseDto<CommentDto>> getComments(@PathVariable UUID postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getComments(postId, pageable));
    }

    @GetMapping("/{postId}/comment/{commentId}/subcomment")
    public ResponseEntity<PageResponseDto<CommentDto>> getSubComments(@PathVariable UUID postId,
                                                           @PathVariable UUID commentId,
                                                           Pageable pageable) {
        return ResponseEntity.ok(commentService.getSubComments(commentId, pageable));
    }
}
