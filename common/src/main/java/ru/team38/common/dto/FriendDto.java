package ru.team38.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class FriendDto {
    private Long id;
    private Boolean isDeleted;
    private String photo;
    private StatusCode statusCode;
    private String firstName;
    private String lastName;
    private String city;
    private String country;
    private LocalDate birthDate;
    private Boolean isOnline;
    private UUID accountFrom;
    private UUID accountTo;
    private StatusCode previousStatus;
    private Short rating;
}
