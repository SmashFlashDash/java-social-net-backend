package ru.team38.common.dto.dialog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Данные сообщения")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    @Schema(description = "Время сообщения")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime time;

    @Schema(description = "ID первого собеседника")
    private UUID conversationPartner1;

    @Schema(description = "ID второго собеседника")
    private UUID conversationPartner2;

    @Schema(description = "Текст сообщения")
    private String messageText;

    @Schema(description = "Статус прочтения сообщения")
    private ReadStatusDto readStatus;

    @Schema(description = "ID диалога")
    private UUID dialogId;
}
