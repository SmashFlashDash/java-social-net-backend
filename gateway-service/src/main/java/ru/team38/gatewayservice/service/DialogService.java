package ru.team38.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.dialog.MessageShortDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.gatewayservice.clients.CommunicationsServiceClient;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService {
    private final CommunicationsServiceClient communicationsServiceClient;

    public Page<DialogDto> getDialogs(Pageable pageable) {
        return communicationsServiceClient.getDialogs(pageable).getBody();
    }

    public CountDto getUnreadMessagesCount() {
        return communicationsServiceClient.getUnreadMessagesCount().getBody();
    }

    public DialogDto getDialogByRecipientId(UUID id) {
        return communicationsServiceClient.getDialogByRecipientId(id).getBody();
    }

    public Page<MessageShortDto> getMessages(UUID recipientId, Pageable pageable) {
        return communicationsServiceClient.getMessages(recipientId, pageable).getBody();
    }
}
