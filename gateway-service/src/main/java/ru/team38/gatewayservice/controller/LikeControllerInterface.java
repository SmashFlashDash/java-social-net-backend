package ru.team38.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ru.team38.common.dto.like.CreateLikeDto;
import ru.team38.common.dto.like.LikeDto;

import java.util.UUID;

@Schema(description = "Управление лайками")
public interface LikeControllerInterface {

    @Operation(summary = "Получение лайка по посту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лайк успешно получен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    LikeDto getLike(@RequestBody @Schema(description = "Данные для создания лайка",
            implementation = CreateLikeDto.class) CreateLikeDto createLikeDto,
                    @PathVariable @Parameter(description = "ID поста") UUID postId);

    @Operation(summary = "Удаление лайка по посту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лайк успешно удален"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> deleteLike(@PathVariable @Parameter(description = "ID поста") UUID postId);

    @Operation(summary = "Получение лайка по комментарию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лайк успешно получен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    LikeDto getLikeByComment(@PathVariable @Parameter(description = "ID поста") UUID postId,
                             @PathVariable @Parameter(description = "ID комментария") UUID commentId);

    @Operation(summary = "Удаление лайка по комментарию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лайк успешно удален"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> deleteLikeByComment(@PathVariable @Parameter(description = "ID поста") UUID postId,
                                               @PathVariable @Parameter(description = "ID комментария") UUID commentId);
}
