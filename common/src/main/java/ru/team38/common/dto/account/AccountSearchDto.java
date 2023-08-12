package ru.team38.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.StatusCode;

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
  private List<String> blockedByIds;
  private String author;
  private String firstName;
  private String lastName;
  private LocalDate maxBirthDate;
  private LocalDate minBirthDate;
  private String city;
  private String country;
  private boolean isBlocked;
  private StatusCode statusCode;
  private Integer ageTo;
  private Integer ageFrom;
}
