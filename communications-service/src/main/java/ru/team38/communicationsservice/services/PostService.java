package ru.team38.communicationsservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.post.ContentPostDto;
import ru.team38.common.dto.post.CreatePostDto;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.dto.post.PostSearchDto;
import ru.team38.communicationsservice.data.repositories.PostRepository;
import ru.team38.communicationsservice.exceptions.NotFoundPostExceptions;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtService jwtService;

    public ContentPostDto getPost(HttpServletRequest request, PostSearchDto postSearchDto) throws NotFoundPostExceptions {
        try {
            if (postSearchDto.getWithFriends() == null){
                postSearchDto.setWithFriends(false);
            }
            String emailUser = jwtService.getUsernameFromToken(request);
            List<PostDto> listPosts = postRepository.getPostDtosByEmail(postSearchDto, emailUser);
            if (listPosts == null) {
                throw new NotFoundPostExceptions("Post not found.");
            }
            return new ContentPostDto(new ArrayList<>(listPosts));
        } catch (Exception e) {
            log.error("Error occurred while retrieving posts: {}", e.getMessage());
            throw e;
        }
    }

    public PostDto createPost(HttpServletRequest request, CreatePostDto createPostDto) throws NotFoundPostExceptions {
        try {
            String emailUser = jwtService.getUsernameFromToken(request);
            PostDto post = postRepository.createPost(createPostDto, emailUser);
            if (post == null) {
                throw new NotFoundPostExceptions("Post not found.");
            }
            return post;
        } catch (Exception e) {
            log.error("Error occurred while retrieving create post: {}", e.getMessage());
            throw e;
        }
    }

    public PostDto updatePost(CreatePostDto createPostDto) throws NotFoundPostExceptions {
        try {
            PostDto post = postRepository.updatePost(createPostDto);
            if (post == null) {
                throw new NotFoundPostExceptions("Post not found.");
            }
            return post;
        } catch (Exception e) {
            log.error("Error occurred while retrieving update post: {}", e.getMessage());
            throw e;
        }
    }

    public PostDto getPostById(Long id) throws NotFoundPostExceptions {
        try {
            PostDto post = postRepository.getPostDtoById(id);
            if (post == null) {
                throw new NotFoundPostExceptions("Post not found.");
            }
            return post;
        } catch (Exception e) {
            log.error("Error occurred while retrieving update post: {}", e.getMessage());
            throw e;
        }
    }

    public void deletePost(Long id){
        postRepository.deletePostById(id);
    }
}
