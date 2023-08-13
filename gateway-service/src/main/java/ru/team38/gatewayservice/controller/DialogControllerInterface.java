package ru.team38.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.dialog.MessageShortDto;
import ru.team38.common.dto.other.CountDto;

import java.util.UUID;

@Schema(description = "Управление диалогами")
public interface DialogControllerInterface {

    @Operation(summary = "Получение списка диалогов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список диалогов успешно получен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    Page<DialogDto> getDialogs(Pageable pageable);

    @Operation(summary = "Получение количества непрочитанных сообщений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество непрочитанных сообщений успешно получено"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    CountDto getUnreadMessagesCount();

    @Operation(summary = "Получение диалога по ID получателя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Диалог успешно получен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    DialogDto getDialogByRecipientId(@PathVariable @Parameter(name = "id", description = "ID получателя") UUID recipientId);

    @Operation(summary = "Получение сообщений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сообщения успешно получены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    Page<MessageShortDto> getMessages(@Parameter(description = "ID получателя")
                                      @RequestParam(value = "recipientId", required = false) UUID recipientId,
                                      Pageable pageable);
}
