package ru.team38.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountSearchDto {
  private UUID id;
  private boolean isDeleted;
  private List<String> ids;
  private String blockedByIds;
  private String author;
  private String firstName;
  private String lastName;
  private LocalDate maxBirthDate;
  private LocalDate minBirthDate;
  private String city;
  private String country;
  private boolean isBlocked;
  private Integer ageTo;
  private Integer ageFrom;
}
