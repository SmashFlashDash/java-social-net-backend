package ru.team38.common.dto;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {
    private String id;
    private boolean isDeleted;
    private String title;
    private String countryId;
}