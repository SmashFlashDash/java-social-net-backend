package ru.team38.userservice.data.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginForm {
    @NotNull
    public String email;
    @NotNull
    public String password;
}