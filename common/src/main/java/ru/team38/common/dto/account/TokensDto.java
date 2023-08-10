package ru.team38.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class TokensDto {
    Long id;
    UUID accountId;
    String tokenType;
    String token;
    Boolean isValid;
    ZonedDateTime expiration;
    String deviceUuid;
}
