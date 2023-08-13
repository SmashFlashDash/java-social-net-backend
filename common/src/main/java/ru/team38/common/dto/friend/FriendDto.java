package ru.team38.common.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.StatusCode;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Данные отношений")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {

    @Schema(description = "ID отношений")
    private Long id;

    @Schema(description = "Удалены ли отношения")
    private Boolean isDeleted;

    @Schema(description = "Фотография объекта отношений")
    private String photo;

    @Schema(description = "Статус код отношения субъекта отношений к объекту отношений")
    private StatusCode statusCode;

    @Schema(description = "Имя объекта отношений")
    private String firstName;

    @Schema(description = "Фамилия объекта отношений")
    private String lastName;

    @Schema(description = "Город объекта отношений")
    private String city;

    @Schema(description = "Страна объекта отношений")
    private String country;

    @Schema(description = "Дата рождения объекта отношений")
    private LocalDate birthDate;

    @Schema(description = "Онлайн ли объект отношений")
    private Boolean isOnline;

    @Schema(description = "Аккаунт субъект отношений")
    private UUID accountFrom;

    @Schema(description = "Аккаунт объект отношений")
    private UUID accountTo;

    @Schema(description = "Предыдущий статус отношений")
    private StatusCode previousStatus;

    @Schema(description = "Рейтинг отношений")
    private Short rating;
}
