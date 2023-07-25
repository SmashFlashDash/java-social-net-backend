package ru.team38.communicationsservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.team38.common.dto.storage.FileType;
import ru.team38.common.dto.storage.FileUriResponse;
import ru.team38.communicationsservice.exceptions.StatusException;
import ru.team38.communicationsservice.services.StorageService;

@RequestMapping("/api/v1/storage")
@RestController
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<FileUriResponse> getUploadedFileUri(@RequestParam FileType type, @RequestPart MultipartFile file) throws StatusException {
        return ResponseEntity.ok(storageService.uploadFile(type, file));
    }
}
