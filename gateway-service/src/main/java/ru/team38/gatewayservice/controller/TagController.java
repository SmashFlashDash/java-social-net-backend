package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.team38.common.dto.post.TagDto;
import ru.team38.gatewayservice.service.PostService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TagController implements TagControllerInterface {

    private final PostService postService;

    @Override
    @GetMapping("/api/v1/tag")
    public List<TagDto> getTag(@RequestParam(value = "name", required = false) String tag){
        log.info("Executing getPost request");
        return postService.getTag(tag);
    }
}
