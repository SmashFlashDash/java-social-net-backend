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
import ru.team38.common.dto.account.AccountDto;
import ru.team38.common.dto.account.AccountSearchDto;
import ru.team38.common.dto.other.PageDto;
import ru.team38.common.dto.other.PageResponseDto;

import java.util.UUID;

@Schema(description = "Управление аккаунтами")
public interface AccountControllerInterface {

    @Operation(summary = "Создание аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<AccountDto> createAccount(@RequestBody @Schema(description = "Данные аккаунта", 
            implementation = AccountDto.class) AccountDto accountDto);

    @Operation(summary = "Получение аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно получен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    AccountDto getAccount();

    @Operation(summary = "Обновление аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    AccountDto updateAccount(@RequestBody @Schema(description = "Данные аккаунта",
            implementation = AccountDto.class) AccountDto account);

    @Operation(summary = "Удаление аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно удален"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> deleteAccount();

    @Operation(summary = "Получение аккаунта по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно получен по ID"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @Parameter(name = "id", description = "ID аккаунта")
    AccountDto getAccountById(@PathVariable @Parameter(name = "id", description = "ID аккаунта") UUID id);

    @Operation(summary = "Поиск аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт(ы) успешно найден(ы)"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @Parameter(name = "accountSearchDto", description = "Критерии поиска", required = true)
    @Parameter(name = "pageDto", description = "Информация о странице", required = true)
    PageResponseDto<AccountDto> findAccount(AccountSearchDto accountSearchDto, PageDto pageDto);

    @Operation(
            summary = "Поиск аккаунтов по статус-коду отношений",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Аккаунт(ы) успешно найден(ы)"),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @Parameter(name = "accountSearchDto", description = "Критерии поиска", required = true)
    @Parameter(name = "pageDto", description = "Информация о странице", required = true)
    PageResponseDto<AccountDto> findAccountByStatusCode(AccountSearchDto accountSearchDto, PageDto pageDto);
}
