package ru.team38.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.comment.*;
import ru.team38.gatewayservice.clients.CommunicationsServiceClient;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommunicationsServiceClient communicationsServiceClient;

    public CommentDto createComment(Long postId, Map<String, String> payload) {
        return communicationsServiceClient.createComment(postId, payload).getBody();
    }

    public CommentDto updateComment(Long postId, CommentUpdateDto commentUpdateDto) {
        return communicationsServiceClient.updateComment(postId, commentUpdateDto).getBody();
    }

    public String deleteComment(Long postId, UUID commentId) {
        return communicationsServiceClient.deleteComment(postId, commentId).getBody();
    }

    public CommentSearchDto getComments(Long postId, Pageable pageable) {
        return communicationsServiceClient.getComments(postId, pageable).getBody();
    }

    public CommentSearchDto getSubComments(Long postId, UUID commentId, Pageable pageable) {
        return communicationsServiceClient.getSubComments(postId, commentId, pageable).getBody();
    }
}
