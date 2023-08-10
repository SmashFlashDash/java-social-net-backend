package ru.team38.common.dto.account;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm {
    @Email(message = "Incorrect email format")
    private String email;
    private String password;
}
