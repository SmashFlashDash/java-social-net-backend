package ru.team38.common.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageableDto {
    private SortDto sort;
    private int pageNumber;
    private int pageSize;
    private int offset;
    private boolean unpaged;
    private boolean paged;
}
