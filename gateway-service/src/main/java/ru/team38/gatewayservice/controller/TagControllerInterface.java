package ru.team38.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestParam;
import ru.team38.common.dto.post.TagDto;

import java.util.List;

@Schema(description = "Управление тегами")
public interface TagControllerInterface {

    @Operation(summary = "Получение тега")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тег(и) успешно получен(ы)"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @Parameter(name = "name", description = "Имя тега", required = false)
    List<TagDto> getTag(@RequestParam(value = "name", required = false)
                        @Parameter(description = "Имя тега") String tag);
}
