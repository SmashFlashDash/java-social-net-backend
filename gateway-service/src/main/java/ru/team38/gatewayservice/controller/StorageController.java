package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.team38.common.dto.storage.FileType;
import ru.team38.common.dto.storage.FileUriResponse;
import ru.team38.gatewayservice.service.PostService;

@RequestMapping("/api/v1/storage")
@RestController
@RequiredArgsConstructor
public class StorageController implements StorageControllerInterface {
    private final PostService postService;

    @Override
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileUriResponse getUploadedFileUri(@RequestParam FileType type, @RequestPart MultipartFile file) {
        return postService.getUploadedFileUri(type, file);
    }
}
