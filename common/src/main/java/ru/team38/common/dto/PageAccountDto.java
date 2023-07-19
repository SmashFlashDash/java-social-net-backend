package ru.team38.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CopyOnWriteArraySet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageAccountDto {
    private Integer totalElements;
    private Integer totalPages;
    private Sort sort;
    private Integer numberOfElements;
    private PageableObject pageable;
    private Boolean first;
    private Boolean last;
    private Integer size;
    private CopyOnWriteArraySet<AccountDto> content;
    private Integer number;
    private Boolean empty;

    {
        content = new CopyOnWriteArraySet<>();
    }

    public void setAccount(AccountDto account) {
        content.add(account);
    }
}
