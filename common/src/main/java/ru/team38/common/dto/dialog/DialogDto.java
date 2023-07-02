package ru.team38.common.dto.dialog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DialogDto {
    private UUID id;
    private Boolean isDeleted;
    private Integer unreadCount;
    private UUID conversationPartner1;
    private UUID conversationPartner2;
    private List<MessageDto> lastMessage;
}
