package ru.team38.common.dto.dialog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.PageableObject;
import ru.team38.common.dto.Sort;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDialogDto {
    private Integer totalPages;
    private Integer totalElements;
    private Sort sort;
    private Integer numberOfElements;
    private PageableObject pageable;
    private boolean first;
    private boolean last;
    private Integer size;
    private List<DialogDto> content;
    private Integer number;
    private boolean empty;
}
