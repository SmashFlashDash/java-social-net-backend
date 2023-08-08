package ru.team38.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.team38.common.dto.storage.FileType;
import ru.team38.common.dto.storage.FileUriResponse;

@Schema(description = "Управление хранилищем")
public interface StorageControllerInterface {

    @Operation(summary = "Загрузка файла")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно загружен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @Parameter(name = "type", description = "Тип файла", required = true)
    FileUriResponse getUploadedFileUri(@RequestParam @Parameter(description = "Тип файла") FileType type,
                                       @RequestPart @Parameter(description = "Файл") MultipartFile file);
}
