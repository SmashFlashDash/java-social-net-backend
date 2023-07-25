package ru.team38.common.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.comment.PageableDto;
import ru.team38.common.dto.comment.SortDto;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentPostDto {
    private List<PostDto> content;
    private PageableDto pageable;
    private int totalElements;
    private boolean last;
    private int totalPages;
    private SortDto sort;
    private int numberOfElements;
    private boolean first;
    private int size;
    private int number;
    private boolean empty;
}
