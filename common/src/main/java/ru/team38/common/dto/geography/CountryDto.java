package ru.team38.common.dto.geography;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Данные страны")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto {

    @Schema(description = "ID страны")
    private Long id;

    @Schema(description = "Удалена ли страна")
    private boolean isDeleted;

    @Schema(description = "Название страны")
    private String title;

    @ArraySchema(schema = @Schema(description = "Список городов"), minItems = 0, uniqueItems = true)
    private List<String> cities;
}
