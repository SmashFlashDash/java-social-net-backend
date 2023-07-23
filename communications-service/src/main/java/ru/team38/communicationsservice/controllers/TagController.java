package ru.team38.communicationsservice.controllers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.post.*;
import ru.team38.communicationsservice.exceptions.NotFoundPostExceptions;
import ru.team38.communicationsservice.services.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tag")
@RequiredArgsConstructor
public class TagController {
    private final PostService postService;
    @GetMapping()
    public ResponseEntity<List<TagDto>> getTag(@RequestParam(value = "name", required = false) String tag) throws NotFoundPostExceptions {
        return ResponseEntity.ok(postService.getTag(tag));
    }
}
