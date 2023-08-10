package ru.team38.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.dto.comment.CommentUpdateDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.gatewayservice.clients.CommunicationsServiceClient;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommunicationsServiceClient communicationsServiceClient;

    public CommentDto createComment(UUID postId, Map<String, String> payload) {
        return communicationsServiceClient.createComment(postId, payload).getBody();
    }

    public CommentDto updateComment(UUID postId, CommentUpdateDto commentUpdateDto) {
        return communicationsServiceClient.updateComment(postId, commentUpdateDto).getBody();
    }

    public String deleteComment(UUID postId, UUID commentId) {
        return communicationsServiceClient.deleteComment(postId, commentId).getBody();
    }

    public PageResponseDto<CommentDto> getComments(UUID postId, Pageable pageable) {
        return communicationsServiceClient.getComments(postId, pageable).getBody();
    }

    public PageResponseDto<CommentDto> getSubComments(UUID postId, UUID commentId, Pageable pageable) {
        return communicationsServiceClient.getSubComments(postId, commentId, pageable).getBody();
    }
}
