package ru.team38.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterDto {
    private final String email;
    private final String password1;
    private final String password2;
    private final String firstName;
    private final String lastName;
    private final String captchaCode;
    private final String captchaSecret;
}
