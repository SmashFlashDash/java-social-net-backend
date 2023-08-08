package ru.team38.common.dto.dialog;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(description = "Данные диалога")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DialogDto {

    @Schema(description = "Количество непрочитанных сообщений")
    private Integer unreadCount;

    @Schema(description = "ID первого собеседника")
    private UUID conversationPartner1;

    @Schema(description = "ID второго собеседника")
    private UUID conversationPartner2;

    @ArraySchema(schema = @Schema(description = "Последние сообщения"), minItems = 0, uniqueItems = true)
    private List<MessageDto> lastMessage;
}
