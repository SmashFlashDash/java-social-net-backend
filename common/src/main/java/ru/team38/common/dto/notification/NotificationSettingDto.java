package ru.team38.common.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingDto {
    UUID id;
    Boolean enablePost;
    Boolean enablePostComment;
    Boolean enableCommentComment;
    Boolean enableMessage;
    Boolean enableFriendRequest;
    Boolean enableFriendBirthday;
    Boolean enableSendEmailMessage;
    Boolean isDeleted;
}
