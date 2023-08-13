package ru.team38.common.dto.other;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Счетчик")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountDto {

    @Schema(description = "Количество")
    private Integer count;
}
