package ru.team38.common.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.comment.PageableDto;
import ru.team38.common.dto.comment.SortDto;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsPageDto {
    private int totalElements;
    private int totalPages;
    private int number;
    private int size;
    private List<DataTimestampDto> content = new ArrayList<>();
    private SortDto sort;
    private boolean first;
    private boolean last;
    private long numberOfElements;
    private PageableDto pageable;
    private boolean empty;
}
