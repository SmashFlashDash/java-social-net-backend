package ru.team38.common.dto.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortDto {
    private boolean unsorted;
    private boolean sorted;
    private boolean empty;
}
