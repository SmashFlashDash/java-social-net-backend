package ru.team38.gatewayservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import ru.team38.common.dto.post.ContentPostDto;
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

    public ContentPostDto getPost(Boolean withFriends,
                                  List<String> sort,
                                  Boolean isDeleted,
                                  UUID accountIds,
                                  List<String> tags,
                                  String dateFrom,
                                  String dateTo,
                                  String author,
                                  Pageable pageable) {
        try {
            ResponseEntity<ContentPostDto> responseEntity = communicationsServiceClient.getPost(
                    withFriends,
                    sort,
                    isDeleted,
                    accountIds,
                    tags,
                    dateFrom,
                    dateTo,
                    author,
                    pageable);
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }
    public PostDto getCreatePost(@RequestBody CreatePostDto createPostDto) {
        try {
            ResponseEntity<PostDto> responseEntity = communicationsServiceClient.getCreatePost(createPostDto);
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }
    public PostDto getUpdatePost(@RequestBody CreatePostDto createPostDto) {
        try {
            ResponseEntity<PostDto> responseEntity = communicationsServiceClient.getUpdatePost(createPostDto);
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }
    public PostDto getPostById(UUID id) {
        try {
            ResponseEntity<PostDto> responseEntity = communicationsServiceClient.getPostById(id);
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }
    public ResponseEntity<String> deletePost(UUID id) {
        return communicationsServiceClient.deletePost(id);
    }

    public List<TagDto> getTag(String tag){
        try {
            ResponseEntity<List<TagDto>> responseEntity = communicationsServiceClient.getTag(tag);
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }

    public FileUriResponse getUploadedFileUri(FileType type, MultipartFile file) {
        return communicationsServiceClient.getUploadedFileUri(type, file).getBody();
    }
}
