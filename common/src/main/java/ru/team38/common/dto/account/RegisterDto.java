package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Данные для регистрации")
@Data
@Builder
@AllArgsConstructor
public class RegisterDto {
    @Schema(description = "Электронная почта")
    private final String email;
    @Schema(description = "Пароль")
    private final String password1;
    @Schema(description = "Подтверждение пароля")
    private final String password2;
    @Schema(description = "Имя")
    private final String firstName;
    @Schema(description = "Фамилия")
    private final String lastName;
    @Schema(description = "Код капчи")
    private final String captchaCode;
    @Schema(description = "Секрет капчи")
    private final String captchaSecret;
}
