package ru.team38.common.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.team38.common.dto.other.StatusCode;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendShortDto {
    private UUID id;
    private Boolean isDeleted;
    private StatusCode statusCode;
    private UUID friendId;
    private UUID idFriend;
    private StatusCode previousStatusCode;
    private Short rating;
}