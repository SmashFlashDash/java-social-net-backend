package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.dialog.MessageShortDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.gatewayservice.service.DialogService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogController {
    private final DialogService dialogService;

    @GetMapping()
    public Page<DialogDto> getDialogs(Pageable pageable) {
        log.info("Executing getDialogs request");
        return dialogService.getDialogs(pageable);
    }

    @GetMapping("/unread")
    public CountDto getUnreadMessagesCount(){
        log.info("Executing getUnreadMessagesCount request");
        return dialogService.getUnreadMessagesCount();
    }

    @GetMapping("/recipientId/{id}")
    public DialogDto getDialogByRecipientId(@PathVariable(value = "id") UUID recipientId) {
        log.info("Executing getDialogByRecipientId request");
        return dialogService.getDialogByRecipientId(recipientId);
    }

    @GetMapping("/messages")
    public Page<MessageShortDto> getMessages
            (@RequestParam(value = "recipientId") UUID recipientId, Pageable pageable) {
        log.info("Executing getDialogMessagesByRecipientId request");
        return dialogService.getMessages(recipientId, pageable);
    }
}
