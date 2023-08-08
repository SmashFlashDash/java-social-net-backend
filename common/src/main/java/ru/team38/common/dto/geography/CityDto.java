package ru.team38.common.dto.geography;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Данные города")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {

    @Schema(description = "ID города")
    private Long id;

    @Schema(description = "Удален ли город")
    private boolean isDeleted;

    @Schema(description = "Название города")
    private String title;

    @Schema(description = "ID страны")
    private Long countryId;
}