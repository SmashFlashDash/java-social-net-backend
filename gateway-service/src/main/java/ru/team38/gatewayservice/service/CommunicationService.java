package ru.team38.gatewayservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.team38.common.dto.post.ContentPostDto;
import ru.team38.common.dto.post.CreatePostDto;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.dto.post.TagDto;
import ru.team38.gatewayservice.clients.CommunicationsServiceClient;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationService {
    private final CommunicationsServiceClient communicationsServiceClient;

    public ContentPostDto getPost(Boolean withFriends,
                                  Integer page,
                                  List<String> sort,
                                  Boolean isDeleted,
                                  Integer size,
                                  UUID accountIds,
                                  List<String> tags,
                                  String dateFrom,
                                  String dateTo,
                                  String author) {
        try {
            ResponseEntity<ContentPostDto> responseEntity = communicationsServiceClient.getPost(
                    withFriends,
                    page,
                    sort,
                    isDeleted,
                    size,
                    accountIds,
                    tags,
                    dateFrom,
                    dateTo,
                    author);
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
    public PostDto getPostById(Long id) {
        try {
            ResponseEntity<PostDto> responseEntity = communicationsServiceClient.getPostById(id);
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }
    public ResponseEntity<String> deletePost(Long id) {
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
}
