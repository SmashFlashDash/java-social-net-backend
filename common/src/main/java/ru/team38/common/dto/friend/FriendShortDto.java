package ru.team38.common.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.StatusCode;

import java.util.UUID;

@Schema(description = "Краткая информация об отношениях")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendShortDto {

    @Schema(description = "ID")
    private UUID id;

    @Schema(description = "Флаг удаления")
    private Boolean isDeleted;

    @Schema(description = "Статус код")
    private StatusCode statusCode;

    @Schema(description = "ID субъекта")
    private UUID friendId;

    @Schema(description = "ID объекта")
    private UUID idFriend;

    @Schema(description = "Предыдущий статус код")
    private StatusCode previousStatusCode;

    @Schema(description = "Рейтинг")
    private Short rating;
}
