package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Данные токенов")
@Data
@AllArgsConstructor
public class TokensDto {
    @Schema(description = "ID")
    Long id;
    @Schema(description = "ID аккаунта")
    UUID accountId;
    @Schema(description = "Тип токена")
    String tokenType;
    @Schema(description = "Токен")
    String token;
    @Schema(description = "Действителен ли токен")
    Boolean isValid;
    @Schema(description = "Срок действия токена")
    ZonedDateTime expiration;
    @Schema(description = "UUID устройства")
    String deviceUuid;
}
