package ru.team38.common.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SortDto {
    private boolean unsorted;
    private boolean sorted;
    private boolean empty;
}
