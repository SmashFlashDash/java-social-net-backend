package ru.team38.common.dto.other;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Данные страницы")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {

  @Schema(description = "Страница (номер)")
  private Integer page;

  @Schema(description = "Размер")
  private Integer size;

  @Schema(description = "Сортировка")
  private List<String> sort;
}
