package ru.team38.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageFriendShortDto {
    private Integer totalElements;
    private Integer totalPages;
    private Integer number;
    private Integer size;
    private List<Object> content;
    private Sort sort;
    private Boolean first;
    private Boolean last;
    private Integer numberOfElements;
    private PageableObject pageable;
    private Boolean empty;
}
