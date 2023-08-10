package ru.team38.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CaptchaDto {
    private String secret;
    private String image;
}