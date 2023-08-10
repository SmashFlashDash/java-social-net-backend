package ru.team38.common.dto.other;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private Integer totalElements;
    private Integer totalPages;
    private Integer number;
    private Integer size;
    private Boolean first;
    private Boolean last;
    private Integer numberOfElements;
    private Boolean empty;
    private SortDto sort;
    private PageableDto pageable;
    private List<T> content;

    public void addToContent(T item) {
        if (content == null) {
            content = new ArrayList<>();
        }
        content.add(item);
    }

    public List<T> getContent() {
        return content == null ? new ArrayList<>() : content;
    }
}
