package ru.team38.common.dto.account;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.StatusCode;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Форма поиска аккаунта")
public class AccountSearchDto {
  @Schema(description = "ID аккаунта")
  private UUID id;

  @Schema(description = "Флаг деактивации аккаунта")
  private boolean isDeleted;

  @ArraySchema(
          arraySchema = @Schema(description = "Список ID аккаунтов",
                  example = "[\"00000000-0000-0000-0000-000000000000\",\"11111111-1111-1111-1111-111111111111\"]"),
          uniqueItems = true,
          minItems = 0
  )
  private List<String> ids;

  @ArraySchema(
          arraySchema = @Schema(description = "ID заблокировавших аккаунтов",
                  example = "[\"00000000-0000-0000-0000-000000000000\",\"11111111-1111-1111-1111-111111111111\"]"),
          uniqueItems = true,
          minItems = 0
  )
  private List<String> blockedByIds;

  @Schema(description = "Автор аккаунта")
  private String author;

  @Schema(description = "Имя аккаунта")
  private String firstName;

  @Schema(description = "Фамилия аккаунта")
  private String lastName;

  @Schema(description = "Максимальная дата рождения")
  private LocalDate maxBirthDate;

  @Schema(description = "Минимальная дата рождения")
  private LocalDate minBirthDate;

  @Schema(description = "Город")
  private String city;

  @Schema(description = "Страна")
  private String country;

  @Schema(description = "Флаг блокировки")
  private boolean isBlocked;

  @Schema(description = "Статус код отношений")
  private StatusCode statusCode;

  @Schema(description = "Максимальный возраст")
  private Integer ageTo;

  @Schema(description = "Минимальный возраст")
  private Integer ageFrom;
}
