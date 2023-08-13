package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для смены пароля")
public class ChangePasswordDto {
    @Schema(description = "Старый пароль")
    private String passwordOld;
    @Schema(description = "Новый пароль")
    private String passwordNew;
}
