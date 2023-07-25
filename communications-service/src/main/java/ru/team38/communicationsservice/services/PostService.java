package ru.team38.communicationsservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.dto.post.ContentPostDto;
import ru.team38.common.dto.post.CreatePostDto;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.dto.post.PostSearchDto;

import ru.team38.common.dto.post.*;
import ru.team38.communicationsservice.services.utils.ConditionUtil;
import ru.team38.communicationsservice.data.repositories.PostRepository;
import ru.team38.communicationsservice.services.utils.DtoAssembler;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final ConditionUtil conditionUtil;
    private final DtoAssembler dtoAssembler;
    private final PostRepository postRepository;
    private final JwtService jwtService;
    public ContentPostDto getPost(HttpServletRequest request, PostSearchDto postSearchDto, Pageable pageable){
        try {
            ConditionPostDto conditionPostDto = conditionUtil.createConditionPostDto(postSearchDto);
            String emailUser = jwtService.getUsernameFromToken(request);
            List<PostDto> listPosts = postRepository.getPostDtosByEmail(conditionPostDto, emailUser);

            return dtoAssembler.createContentPostDto(listPosts, pageable);
        } catch (Exception e) {
            log.error("Error occurred while retrieving posts: {}", e.getMessage());
            throw e;
        }
    }
    @Transactional
    public PostDto createPost(HttpServletRequest request, CreatePostDto createPostDto){
        try {
            InsertPostDto insertPostDto = dtoAssembler.createInsertPostDto(createPostDto);
            String emailUser = jwtService.getUsernameFromToken(request);

            return postRepository.createPost(insertPostDto, emailUser);
        } catch (Exception e) {
            log.error("Error occurred while retrieving create post: {}", e.getMessage());
            throw e;
        }
    }
    @Transactional
    public PostDto updatePost(CreatePostDto createPostDto){
        try {
            InsertPostDto insertPostDto = dtoAssembler.createInsertPostDto(createPostDto);
            return postRepository.updatePost(insertPostDto, createPostDto.getId());
        } catch (Exception e) {
            log.error("Error occurred while retrieving update post: {}", e.getMessage());
            throw e;
        }
    }

    public PostDto getPostById(UUID id){
        try {
            return postRepository.getPostDtoById(id);
        } catch (Exception e) {
            log.error("Error occurred while retrieving post: {}", e.getMessage());
            throw e;
        }
    }
    @Transactional
    public void deletePost(UUID id){
        postRepository.deletePostById(id);
    }
    public List<TagDto> getTag(String nameTag){
        try {
            return postRepository.getTags(nameTag);
        } catch (Exception e) {
            log.error("Error occurred while retrieving get tag: {}", e.getMessage());
            throw e;
        }
    }
}
