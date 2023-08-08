package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.StatusCode;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация об аккаунте")
public class AccountDto {
    @Schema(description = "Уникальный идентификатор аккаунта")
    private UUID id;

    @Schema(description = "Статус удаления аккаунта")
    private Boolean isDeleted;

    @Schema(description = "Имя пользователя")
    private String firstName;

    @Schema(description = "Фамилия пользователя")
    private String lastName;

    @Schema(description = "Электронная почта пользователя")
    private String email;

    @Schema(description = "Пароль пользователя", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "Телефон пользователя")
    private String phone;

    @Schema(description = "Описание пользователя")
    private String about;

    @Schema(description = "Город пользователя")
    private String city;

    @Schema(description = "Страна пользователя")
    private String country;

    @Schema(description = "Код статуса аккаунта")
    private StatusCode statusCode;

    @Schema(description = "Дата регистрации аккаунта", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private ZonedDateTime regDate;

    @Schema(description = "Дата рождения пользователя")
    private LocalDate birthDate;

    @Schema(description = "Разрешение на отправку сообщений")
    private Boolean messagePermission;

    @Schema(description = "Последнее время онлайн", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private ZonedDateTime lastOnlineTime;

    @Schema(description = "Статус онлайн")
    private Boolean isOnline;

    @Schema(description = "Статус блокировки аккаунта")
    private Boolean isBlocked;

    @Schema(description = "Фотография профиля")
    private String photo;

    @Schema(description = "Обложка профиля")
    private String profileCover;

    @Schema(description = "Дата создания аккаунта", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private ZonedDateTime createdOn;

    @Schema(description = "Дата обновления аккаунта", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private ZonedDateTime updatedOn;
}
