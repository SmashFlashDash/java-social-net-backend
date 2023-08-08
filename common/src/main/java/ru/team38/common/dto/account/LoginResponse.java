package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Форма ответа с jwt")
public class LoginResponse {
    @Schema(description = "Токен доступа")
    private String accessToken;

    @Schema(description = "Токен обновления")
    private String refreshToken;
}
