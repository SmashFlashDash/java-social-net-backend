package ru.team38.communicationsservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.dialog.MessageShortDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.communicationsservice.exceptions.AccountNotFoundExceptions;
import ru.team38.communicationsservice.exceptions.UnreadMessagesCountNotFoundExceptions;
import ru.team38.communicationsservice.services.DialogService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;

    @GetMapping()
    public ResponseEntity<Page<DialogDto>> getDialogs(Pageable pageable) throws AccountNotFoundExceptions {
        return ResponseEntity.ok(dialogService.getDialogs(pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<CountDto> getUnreadMessagesCount() throws UnreadMessagesCountNotFoundExceptions,
            AccountNotFoundExceptions {
        return ResponseEntity.ok(dialogService.getUnreadMessagesCount());
    }

    @GetMapping("/recipientId/{id}")
    public ResponseEntity<DialogDto> getDialogByRecipientId(@PathVariable UUID id) throws AccountNotFoundExceptions {
        return ResponseEntity.ok(dialogService.getDialogByRecipientId(id));
    }

    @GetMapping("/messages")
    public ResponseEntity<Page<MessageShortDto>> getMessages(
            @RequestParam UUID recipientId, Pageable pageable) throws AccountNotFoundExceptions {
        return ResponseEntity.ok(dialogService.getMessages(recipientId, pageable));
    }
}
