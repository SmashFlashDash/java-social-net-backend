package ru.team38.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.common.dto.post.CreatePostDto;
import ru.team38.common.dto.post.PostDto;

import java.util.List;
import java.util.UUID;

@Schema(description = "Управление постами")
public interface PostControllerInterface {

    @Operation(summary = "Получение списка постов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Посты успешно получены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    PageResponseDto<PostDto> getPost(
            @Parameter(description = "Флаг \"с друзьями\"") @RequestParam(value = "withFriends", required = false) Boolean withFriends,
            @Parameter(description = "Сортировка") @RequestParam(value = "sort", required = false) List<String> sort,
            @Parameter(description = "Удаленные посты включены") @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
            @Parameter(description = "ID аккаунта") @RequestParam(value = "accountIds", required = false) UUID accountIds,
            @Parameter(description = "Теги") @RequestParam(value = "tags", required = false) List<String> tags,
            @Parameter(description = "Дата от") @RequestParam(value = "dateForm", required = false) String dateFrom,
            @Parameter(description = "Дата до") @RequestParam(value = "dateTo", required = false) String dateTo,
            @Parameter(description = "Автор") @RequestParam(value = "author", required = false) String author,
            @Parameter(description = "Текст") @RequestParam(value = "text", required = false) String text,
            Pageable pageable);

    @Operation(summary = "Получение поста по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост успешно получен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    PostDto getPostById(@Parameter(description = "ID поста") @PathVariable UUID id);

    @Operation(summary = "Создание поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост успешно создан"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    PostDto getCreatePost(@RequestBody @Schema(description = "Данные для создания поста",
            implementation = CreatePostDto.class) CreatePostDto createPostDto);

    @Operation(summary = "Обновление поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    PostDto getUpdatePost(@RequestBody @Schema(description = "Данные для обновления поста",
            implementation = CreatePostDto.class) CreatePostDto createPostDto);

    @Operation(summary = "Удаление поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост успешно удален"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> deletePost(@Parameter(description = "ID поста") @PathVariable UUID id);
}
