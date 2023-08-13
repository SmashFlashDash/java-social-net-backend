package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Запрос на обновление токена")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    @Schema(description = "Токен обновления")
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
