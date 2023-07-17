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
public class AccountResultSearchDto {
    private long totalElements;
    private long totalPages;
    private Sort sort;
    private PageDto pageDto;
    private long numberOfElements;
    private PageableObject pageable;
    private Boolean first;
    private Boolean last;
    private long size;
    private CopyOnWriteArraySet<AccountDto> content;
    private long number;
    private Boolean empty;

    {
        content = new CopyOnWriteArraySet<>();
    }

    public void setAccount(AccountDto account) {
        content.add(account);
    }
}
