package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.dialog.PageDialogDto;
import ru.team38.common.dto.dialog.UnreadCountDto;
import ru.team38.common.dto.post.ContentPostDto;
import ru.team38.common.dto.post.CreatePostDto;
import ru.team38.common.dto.post.PostDto;
import ru.team38.gatewayservice.service.CommunicationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommunicationController {
    private final CommunicationService communicationService;
    @GetMapping("/post")
    public ContentPostDto getPost(@RequestParam(value = "withFriends", required = false) Boolean withFriends,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "sort", required = false) List<String> sort,
                                  @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
                                  @RequestParam(value = "size", required = false) Integer size,
                                  @RequestParam(value = "accountIds", required = false) UUID accountIds,
                                  @RequestParam(value = "tags", required = false) List<String> tags,
                                  @RequestParam(value = "dateForm", required = false) String dateFrom,
                                  @RequestParam(value = "dateTo", required = false) String dateTo,
                                  @RequestParam(value = "author", required = false) String author) {
        log.info("Executing getPost request");
        return communicationService.getPost(withFriends, page, sort, isDeleted, size, accountIds, tags, dateFrom, dateTo, author);
    }

    @GetMapping("/dialogs")
    public PageDialogDto getDialogs(@RequestParam(value = "page") Integer page,
                                    @RequestParam(value = "size", required = false, defaultValue = "20")
                                    Integer size,
                                    @RequestParam(value = "sort", required = false) List<String> sort){
        log.info("Executing getDialogs request");
        return communicationService.getDialogs(page, size, sort);
    }

    @GetMapping("/dialogs/unread")
    public UnreadCountDto getUnreadMessagesCount(){
        log.info("Executing getUnreadMessagesCount request");
        return communicationService.getUnreadMessagesCount();
    }

    @GetMapping("/post/{id}")
    public PostDto getPostById(@PathVariable Long id) {
        log.info("Executing getPostById request");
        return communicationService.getPostById(id);
    }
    @PostMapping("/post")
    public PostDto getCreatePost(@RequestBody CreatePostDto createPostDto){
        log.info("Executing getCreatePost request");
        return communicationService.getCreatePost(createPostDto);
    }
    @PutMapping("/post")
    public PostDto getUpdatePost(@RequestBody CreatePostDto createPostDto){
        log.info("Executing getUpdatePost request");
        return communicationService.getUpdatePost(createPostDto);
    }
    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        log.info("Executing deletePost request");
        return communicationService.deletePost(id);
    }
}
