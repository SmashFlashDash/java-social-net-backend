package ru.team38.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import ru.team38.common.dto.account.*;
import ru.team38.common.dto.friend.FriendSearchDto;
import ru.team38.common.dto.friend.FriendShortDto;
import ru.team38.common.dto.geography.CityDto;
import ru.team38.common.dto.geography.CountryDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.common.dto.other.PageDto;
import ru.team38.common.dto.other.PageResponseDto;

import java.util.List;
import java.util.UUID;

@Schema(description = "Управление пользователями")
public interface UserControllerInterface {

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> register(@RequestBody @Schema(description = "Данные для регистрации",
            implementation = RegisterDto.class) RegisterDto registerDto);

    @Operation(summary = "Вход в систему")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    LoginResponse login(@RequestBody @Valid @Schema(description = "Форма для входа",
            implementation = LoginForm.class) LoginForm loginForm);

    @Operation(summary = "Выход из системы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный выход"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> logout();

    @Operation(summary = "Изменение Email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на изменение Email успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> changeEmail(@RequestBody @Schema(description = "DTO с Email",
            implementation = EmailDto.class) EmailDto emailDto);

    @Operation(summary = "Изменение пароля")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на изменение пароля успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> changePassword(@RequestBody @Schema(description = "DTO для смены пароля",
            implementation = ChangePasswordDto.class) ChangePasswordDto passwordDto);

    @Operation(summary = "Обновление токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    LoginResponse refresh(@RequestBody @Valid @Schema(description = "Запрос на обновление access токена",
            implementation = RefreshTokenRequest.class) RefreshTokenRequest refreshTokenRequest);

    @Operation(summary = "Получение капчи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Капча успешно получена"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    CaptchaDto getCaptcha();

    @Operation(summary = "Восстановление пароля")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на восстановление пароля успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> recoverPassword(@RequestBody @Schema(description = "DTO с Email",
            implementation = EmailDto.class) EmailDto emailDto);

    @Operation(summary = "Установка нового пароля")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Новый пароль успешно установлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> setNewPassword(@PathVariable @Parameter(description = "Токен для восстановления пароля") String linkId,
                                          @RequestBody @Schema(description = "Данные для установки нового пароля",
                                                  implementation = NewPasswordDto.class) NewPasswordDto newPasswordDto);

    @Operation(summary = "Получение количества входящих запросов в друзья")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение количества запросов"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    CountDto getIncomingFriendRequests();

    @Operation(summary = "Получение друзей по параметрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Друзья успешно получены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    PageResponseDto<Object> getFriendsByParameters(@RequestBody @Schema(description = "Параметры поиска друзей",
            implementation = FriendSearchDto.class) FriendSearchDto friendSearchDto,
                                                   @RequestBody @Schema(description = "Данные страницы поисковой выдачи",
                                                           implementation = PageDto.class) PageDto pageDto);

    @Operation(summary = "Получение рекомендаций друзей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Рекомендации друзей успешно получены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    List<FriendShortDto> getFriendsRecommendations(@RequestBody @Schema(description = "Параметры поиска друзей",
            implementation = FriendSearchDto.class) FriendSearchDto friendSearchDto);

    @Operation(summary = "Блокировка аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно заблокирован"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<FriendShortDto> blockAccount(@PathVariable @Parameter(description = "ID аккаунта для блокировки") UUID id);

    @Operation(summary = "Разблокировка аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно разблокирован"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<FriendShortDto> unblockAccount(@PathVariable @Parameter(description = "ID аккаунта для разблокировки") UUID id);

    @Operation(summary = "Отправка запроса в друзья")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос в друзья успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<FriendShortDto> makeFriendRequest(@PathVariable @Parameter(description = "ID аккаунта для запроса в друзья") UUID id);

    @Operation(summary = "Одобрение запроса в друзья")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос в друзья успешно одобрен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<FriendShortDto> approveFriendRequest(@PathVariable @Parameter(description = "ID аккаунта для одобрения запроса в друзья") UUID id);

    @Operation(summary = "Удаление отношений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отношения успешно удалены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })

    ResponseEntity<Void> deleteRelationship(@PathVariable @Parameter(description = "ID аккаунта для удаления отношений") UUID id);
    @Operation(summary = "Подписка на аккаунт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подписка успешно оформлена"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<FriendShortDto> getSubscription(@PathVariable @Parameter(description = "ID аккаунта для подписки") UUID id);

    @Operation(summary = "Загрузка геоданных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Геоданные успешно загружены"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<String> loadGeoData();

    @Operation(summary = "Получение списка стран")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список стран успешно получен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<List<CountryDto>> getCountries();

    @Operation(summary = "Получение списка городов по ID страны")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список городов успешно получен"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<List<CityDto>> getCitiesByCountryId(@PathVariable @Parameter(description = "ID страны для получения списка городов") String countryId);
}
