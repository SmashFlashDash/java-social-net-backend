package ru.team38.gatewayservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.dto.comment.CommentUpdateDto;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.dialog.MessageShortDto;
import ru.team38.common.dto.like.CreateLikeDto;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.dto.post.CreatePostDto;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.dto.post.TagDto;
import ru.team38.common.dto.storage.FileType;
import ru.team38.common.dto.storage.FileUriResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "communications-service", url = "${spring.services.communications.url}")
public interface CommunicationsServiceClient {

    @GetMapping("/api/v1/post")
    ResponseEntity<PageResponseDto<PostDto>> getPost(@RequestParam(value = "withFriends", required = false) Boolean withFriends,
                                                     @RequestParam(value = "sort", required = false) List<String> sort,
                                                     @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
                                                     @RequestParam(value = "accountIds", required = false) UUID accountIds,
                                                     @RequestParam(value = "tags", required = false) List<String> tags,
                                                     @RequestParam(value = "dateForm", required = false) String dateFrom,
                                                     @RequestParam(value = "dateTo", required = false) String dateTo,
                                                     @RequestParam(value = "author", required = false) String author,
                                                     @RequestParam(value = "text", required = false) String text,
                                                     Pageable pageable);

    @GetMapping("/api/v1/post/{id}")
    ResponseEntity<PostDto> getPostById(@PathVariable UUID id);

    @PostMapping("/api/v1/post")
    ResponseEntity<PostDto> getCreatePost(@RequestBody CreatePostDto createPostDto);

    @PutMapping("/api/v1/post")
    ResponseEntity<PostDto> getUpdatePost(@RequestBody CreatePostDto createPostDto);

    @DeleteMapping("/api/v1/post/{id}")
    ResponseEntity<String> deletePost(@PathVariable UUID id);

    @PostMapping("/api/v1/post/{postId}/comment")
    ResponseEntity<CommentDto> createComment(@PathVariable UUID postId,
                                             @RequestBody Map<String, String> payload);

    @PutMapping("/api/v1/post/{postId}/comment")
    ResponseEntity<CommentDto> updateComment(@PathVariable UUID postId,
                                             @RequestBody CommentUpdateDto commentUpdateDto);

    @DeleteMapping("/api/v1/post/{postId}/comment/{commentId}")
    ResponseEntity<String> deleteComment(@PathVariable UUID postId,
                                         @PathVariable UUID commentId);

    @GetMapping("/api/v1/post/{postId}/comment")
    ResponseEntity<PageResponseDto<CommentDto>> getComments(@PathVariable UUID postId, Pageable pageable);

    @GetMapping("/api/v1/post/{postId}/comment/{commentId}/subcomment")
    ResponseEntity<PageResponseDto<CommentDto>> getSubComments(@PathVariable UUID postId,
                                                               @PathVariable UUID commentId,
                                                               Pageable pageable);

    @GetMapping("/api/v1/tag")
    ResponseEntity<List<TagDto>> getTag(@RequestParam(value = "name", required = false) String tag);

    @PostMapping(value = "/api/v1/storage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<FileUriResponse> getUploadedFileUri(@RequestParam FileType type, @RequestPart MultipartFile file);

    @PostMapping("/api/v1/post/{postId}/like")
    ResponseEntity<LikeDto> getLikeByPost(@RequestBody CreateLikeDto createLikeDto, @PathVariable UUID postId);

    @DeleteMapping("/api/v1/post/{postId}/like")
    ResponseEntity<String> deleteLikeByPost(@PathVariable UUID postId);

    @PostMapping("/api/v1/post/{postId}/comment/{commentId}/like")
    ResponseEntity<LikeDto> getLikeByComment(@PathVariable UUID postId, @PathVariable UUID commentId);

    @DeleteMapping("/api/v1/post/{postId}/comment/{commentId}/like")
    ResponseEntity<String> deleteLikeByComment(@PathVariable UUID postId, @PathVariable UUID commentId);

    //dialogs endpoints
    @GetMapping("/api/v1/dialogs")
    ResponseEntity<Page<DialogDto>> getDialogs(Pageable pageable);

    @GetMapping("/api/v1/dialogs/unread")
    ResponseEntity<CountDto> getUnreadMessagesCount();

    @GetMapping("/api/v1/dialogs/recipientId/{id}")
    ResponseEntity<DialogDto> getDialogByRecipientId(@PathVariable UUID id);

    @GetMapping("/api/v1/dialogs/messages")
    ResponseEntity<Page<MessageShortDto>> getMessages
            (@RequestParam(value = "recipientId") UUID recipientId, Pageable pageable);
}