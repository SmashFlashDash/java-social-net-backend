package ru.team38.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.dto.post.CreatePostDto;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.dto.post.TagDto;
import ru.team38.common.dto.storage.FileType;
import ru.team38.common.dto.storage.FileUriResponse;
import ru.team38.gatewayservice.clients.CommunicationsServiceClient;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final CommunicationsServiceClient communicationsServiceClient;

    public PageResponseDto<PostDto> getPost(Boolean withFriends, List<String> sort, Boolean isDeleted, UUID accountIds,
                                            List<String> tags, String dateFrom, String dateTo, String author, String text,
                                            Pageable pageable) {
        ResponseEntity<PageResponseDto<PostDto>> responseEntity = communicationsServiceClient.getPost(withFriends,
                sort, isDeleted, accountIds, tags, dateFrom, dateTo, author, text, pageable);
        return responseEntity.getBody();
    }

    public PostDto getCreatePost(@RequestBody CreatePostDto createPostDto) {
        ResponseEntity<PostDto> responseEntity = communicationsServiceClient.getCreatePost(createPostDto);
        return responseEntity.getBody();
    }

    public PostDto getUpdatePost(@RequestBody CreatePostDto createPostDto) {
        ResponseEntity<PostDto> responseEntity = communicationsServiceClient.getUpdatePost(createPostDto);
        return responseEntity.getBody();
    }

    public PostDto getPostById(UUID id) {
        ResponseEntity<PostDto> responseEntity = communicationsServiceClient.getPostById(id);
        return responseEntity.getBody();
    }

    public ResponseEntity<String> deletePost(UUID id) {
        return communicationsServiceClient.deletePost(id);
    }

    public List<TagDto> getTag(String tag) {
        ResponseEntity<List<TagDto>> responseEntity = communicationsServiceClient.getTag(tag);
        return responseEntity.getBody();
    }

    public FileUriResponse getUploadedFileUri(FileType type, MultipartFile file) {
        return communicationsServiceClient.getUploadedFileUri(type, file).getBody();
    }
}
