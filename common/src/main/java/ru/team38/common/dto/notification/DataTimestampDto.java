package ru.team38.common.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Schema(description = "Данные для уведомления с timestamp")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataTimestampDto {

    @Schema(description = "Временна́я метка")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private ZonedDateTime timestamp;

    @Schema(description = "Данные")
    private Object data;
}
