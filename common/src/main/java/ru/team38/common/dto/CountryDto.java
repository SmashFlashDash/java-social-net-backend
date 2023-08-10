package ru.team38.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto {
    private Long id;
    private boolean isDeleted;
    private String title;
    private List<String> cities;
}