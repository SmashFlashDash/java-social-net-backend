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
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.dto.comment.CommentUpdateDto;
import ru.team38.common.dto.other.PageResponseDto;

import java.util.Map;
import java.util.UUID;

@Schema(description = "Управление комментариями")
public interface CommentControllerInterface {

    @Operation(summary = "Создание комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<CommentDto> createComment(@PathVariable @Parameter(description = "ID поста") UUID postId,
                                             @RequestBody @Schema(description = "Текст комментария") Map<String, String> payload);

    @Operation(summary = "Обновление комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<CommentDto> updateComment(@PathVariable @Parameter(description = "ID поста") UUID postId,
                                             @RequestBody @Schema(description = "Данные для обновления комментария",
                                                     implementation = CommentUpdateDto.class) CommentUpdateDto commentUpdateDto);

    @Operation(summary = "Удаление комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно удален"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> deleteComment(@PathVariable @Parameter(description = "ID поста") UUID postId,
                                         @PathVariable @Parameter(description = "ID комментария") UUID commentId);

    @Operation(summary = "Получение комментариев по посту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарии успешно получены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<PageResponseDto<CommentDto>> getComments(@PathVariable @Parameter(description = "ID поста") UUID postId,
                                                            Pageable pageable);

    @Operation(summary = "Получение субкомментариев по комментарию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Субкомментарии успешно получены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<PageResponseDto<CommentDto>> getSubComments(@PathVariable @Parameter(description = "ID поста") UUID postId,
                                                               @PathVariable @Parameter(description = "ID комментария") UUID commentId,
                                                               Pageable pageable);
}
