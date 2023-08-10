package ru.team38.communicationsservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.aspects.LoggingClass;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.dto.comment.CommentUpdateDto;
import ru.team38.common.dto.notification.NotificationTypeEnum;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.dto.other.PageableDto;
import ru.team38.common.dto.other.PublicationType;
import ru.team38.common.dto.other.SortDto;
import ru.team38.common.services.NotificationAddService;
import ru.team38.communicationsservice.data.repositories.CommentRepository;
import ru.team38.communicationsservice.exceptions.BadCommentRequestException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@LoggingClass
public class CommentService {

    private final CommentRepository commentRepository;
    private final JwtService jwtService;
    private final NotificationAddService notificationService;

    public CommentDto createComment(HttpServletRequest request, UUID postId, Map<String, String> payload) {
        if (payload.containsKey("parentId")) {
            return createSubComment(request, postId,
                    UUID.fromString(payload.get("parentId")), payload.get("commentText"));
        }
        String authorName = jwtService.getUsernameFromToken(request);
        CommentDto commentDto = CommentDto.builder()
                .id(UUID.randomUUID())
                .commentType(PublicationType.POST)
                .time(ZonedDateTime.now())
                .timeChanged(ZonedDateTime.now())
                .authorId(commentRepository.getIdByUsername(authorName))
                .commentText(payload.get("commentText"))
                .postId(postId)
                .build();
        commentDto = commentRepository.createComment(commentDto);
        notificationService.addNotification(commentDto.getAuthorId(), commentDto, NotificationTypeEnum.POST_COMMENT);
        return commentDto;
    }

    public CommentDto updateComment(HttpServletRequest request, CommentUpdateDto commentUpdateDto) {
        String usernameFromToken = jwtService.getUsernameFromToken(request);
        String usernameOfCommentCreator = commentRepository.getUsernameByCommentId(commentUpdateDto.getId());
        if(usernameFromToken.equals(usernameOfCommentCreator)) {
            UUID commentId = commentUpdateDto.getId();
            String newCommentText = commentUpdateDto.getCommentText();
            return commentRepository.updateComment(commentId, newCommentText);
        } else {
            throw new BadCommentRequestException("No rights for editing");
        }
    }

    public CommentDto createSubComment(HttpServletRequest request, UUID postId, UUID parentId, String text) {
        String authorName = jwtService.getUsernameFromToken(request);
        CommentDto commentDto = CommentDto.builder()
                .id(UUID.randomUUID())
                .commentType(PublicationType.COMMENT)
                .time(ZonedDateTime.now())
                .timeChanged(ZonedDateTime.now())
                .authorId(commentRepository.getIdByUsername(authorName))
                .commentText(text)
                .postId(postId)
                .parentId(parentId)
                .build();
        commentDto = commentRepository.createComment(commentDto);
        notificationService.addNotification(commentDto.getAuthorId(), commentDto, NotificationTypeEnum.COMMENT_COMMENT);
        return commentDto;
    }

    public void deleteComment(HttpServletRequest request, UUID postId, UUID commentId) {
        String usernameFromToken = jwtService.getUsernameFromToken(request);
        String usernameOfCommentCreator = commentRepository.getUsernameByCommentId(commentId);
        if(usernameFromToken.equals(usernameOfCommentCreator)) {
            commentRepository.deleteComment(postId, commentId);
        } else {
            throw new BadCommentRequestException("No rights for deleting");
        }
    }

    public PageResponseDto<CommentDto> getComments(UUID postId, Pageable pageable) {
        List<CommentDto> comments = commentRepository.getMainComments(postId, pageable);
        return createPageResponseDto(comments, pageable);
    }

    public PageResponseDto<CommentDto> getSubComments(UUID commentId, Pageable pageable) {
        List<CommentDto> comments = commentRepository.getSubComments(commentId, pageable);
        return createPageResponseDto(comments, pageable);
    }

    private PageResponseDto<CommentDto> createPageResponseDto(List<CommentDto> comments, Pageable pageable) {
        boolean isLast = comments.size() <= pageable.getPageSize();

        if (!isLast) {
            comments.remove(comments.size() - 1);
        }

        SortDto sortDto = new SortDto(pageable.getSort().isUnsorted(), pageable.getSort().isSorted(),
                pageable.getSort().isEmpty());
        int totalPages = (comments.size() + pageable.getPageSize() - 1) / pageable.getPageSize();
        boolean isFirst = pageable.getPageNumber() == 0;
        boolean isEmpty = comments.isEmpty();
        PageableDto pageableDto = new PageableDto(sortDto, pageable.getPageNumber(), pageable.getPageSize(),
                (int) pageable.getOffset(), pageable.isUnpaged(), pageable.isPaged());

        return PageResponseDto.<CommentDto>builder()
                .content(comments)
                .last(isLast)
                .totalElements(comments.size())
                .totalPages(totalPages)
                .sort(sortDto)
                .numberOfElements(comments.size())
                .first(isFirst)
                .size(pageable.getPageSize())
                .number(pageable.getPageNumber())
                .empty(isEmpty)
                .pageable(pageableDto)
                .build();
    }
}
