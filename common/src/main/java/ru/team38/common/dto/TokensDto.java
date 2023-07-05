package ru.team38.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokensDto {
    Long id;
    Long accountId;
    String tokenType;
    String token;
    Boolean isValid;
}
