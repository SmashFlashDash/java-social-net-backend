package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Информация о капче")
public class CaptchaDto {
    @Schema(description = "Секретный ключ капчи")
    private String secret;

    @Schema(description = "Изображение капчи с заголовком")
    private String image;
}
