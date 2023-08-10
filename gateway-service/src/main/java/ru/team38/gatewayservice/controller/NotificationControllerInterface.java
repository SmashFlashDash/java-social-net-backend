package ru.team38.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.team38.common.dto.notification.DataTimestampDto;
import ru.team38.common.dto.notification.NotificationSettingDto;
import ru.team38.common.dto.notification.NotificationUpdateDto;
import ru.team38.common.dto.other.PageResponseDto;

import java.util.UUID;

@Schema(description = "Управление уведомлениями")
public interface NotificationControllerInterface {

    @Operation(summary = "Получение уведомлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомления успешно получены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<PageResponseDto<DataTimestampDto>> getNotifications(@RequestHeader("x-lang") String lang);

    @Operation(summary = "Отметить все уведомления как прочитанные")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомления отмечены как прочитанные"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> readAllNotifications();

    @Operation(summary = "Получение количества уведомлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество уведомлений успешно получено"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    DataTimestampDto getNotificationsCount();

    @Operation(summary = "Получение настроек уведомлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Настройки уведомлений успешно получены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<NotificationSettingDto> getNotificationSetting();

    @Operation(summary = "Обновление настроек уведомлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Настройки уведомлений успешно обновлены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<NotificationSettingDto> updateNotificationSetting(@RequestBody @Schema(description = "Данные для обновления настроек уведомлений",
            implementation = NotificationUpdateDto.class) NotificationUpdateDto notificationUpdateDto);

    @Operation(summary = "Установка настроек уведомлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Настройки уведомлений успешно установлены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @Parameter(name = "id", description = "ID аккаунта")
    ResponseEntity<NotificationSettingDto> setNotificationSetting(@PathVariable @Parameter(name = "id", description = "ID аккаунта") UUID id);
}
