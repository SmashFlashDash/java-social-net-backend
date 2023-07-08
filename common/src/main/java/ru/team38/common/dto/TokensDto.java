package ru.team38.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class TokensDto {
    Long id;
    Long accountId;
    String tokenType;
    String token;
    Boolean isValid;
    ZonedDateTime expiration;
    String deviceUuid;
}
