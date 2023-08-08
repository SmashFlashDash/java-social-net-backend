package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO с новым паролем")
public class NewPasswordDto {
    @Schema(description = "Новый пароль")
    String password;
}
