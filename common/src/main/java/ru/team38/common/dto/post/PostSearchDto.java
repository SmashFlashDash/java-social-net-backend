package ru.team38.common.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchDto {
    private Boolean withFriends;
    private Integer page;
    private List <String> sort;
    private Boolean isDeleted;
    private Integer size;
    private UUID accountIds;
    private List<String> tags;
    private String dateFrom;
    private String dateTo;
    private String author;
}
