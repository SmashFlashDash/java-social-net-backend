package ru.team38.common.dto.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Ответ с именем файла")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUriResponse {

    @Schema(description = "Имя файла")
    private String fileName;
}
