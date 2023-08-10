package ru.team38.communicationsservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.dto.post.CreatePostDto;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.dto.post.PostSearchDto;
import ru.team38.communicationsservice.services.PostService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping()
    public ResponseEntity<PageResponseDto<PostDto>> getPost(HttpServletRequest request, PostSearchDto postSearchDto, Pageable pageable){
        return ResponseEntity.ok(postService.getPost(request, postSearchDto, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping()
    public ResponseEntity<PostDto> createPost(HttpServletRequest request, @RequestBody CreatePostDto createPostDto){
        return ResponseEntity.ok(postService.createPost(request, createPostDto));
    }

    @PutMapping()
    public ResponseEntity<PostDto> updatePost(@RequestBody CreatePostDto createPostDto){
        return ResponseEntity.ok(postService.updatePost(createPostDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Пост удален");
    }
}
