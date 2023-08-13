package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Форма входа")
public class LoginForm {
    @Schema(description = "Email пользователя", example = "user@example.com")
    @Email(message = "Incorrect email format")
    private String email;

    @Schema(description = "Пароль пользователя")
    private String password;
}
