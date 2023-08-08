package ru.team38.common.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.StatusCode;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Schema(description = "Форма поиска друзей")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendSearchDto {
    @Schema(description = "ID аккаунта")
    private UUID id;
    @Schema(description = "Флаг удаления")
    private Boolean isDeleted;
    @Schema(description = "ID субъекта отношений")
    private Integer idFrom;
    @Schema(description = "Статус код")
    private StatusCode statusCode;
    @Schema(description = "ID объекта отношений")
    private Integer idTo;
    @Schema(description = "Имя")
    private String firstName;
    @Schema(description = "Дата рождения от")
    private LocalDate birthDateFrom;
    @Schema(description = "Дата рождения до")
    private LocalDate birthDateTo;
    @Schema(description = "Город")
    private String city;
    @Schema(description = "Страна")
    private String country;
    @Schema(description = "Возраст от")
    private Integer ageFrom;
    @Schema(description = "Возраст до")
    private Integer ageTo;
    @Schema(description = "Предыдущий статус код отношений")
    private StatusCode previousStatusCode;

    public boolean allNull() {
        return Stream.of(
                firstName,
                country,
                city,
                ageFrom,
                ageTo
        ).allMatch(Objects::isNull);
    }
}
