package ru.team38.communicationsservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.team38.common.dto.PageDto;
import ru.team38.common.dto.dialog.PageDialogDto;
import ru.team38.common.dto.dialog.UnreadCountDto;
import ru.team38.communicationsservice.exceptions.AccountNotFoundExceptions;
import ru.team38.communicationsservice.exceptions.UnreadMessagesCountNotFoundExceptions;
import ru.team38.communicationsservice.services.DialogService;

@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;

    @GetMapping()
    public ResponseEntity<PageDialogDto> getDialogs(PageDto pageDto) throws AccountNotFoundExceptions {
        return ResponseEntity.ok(dialogService.getDialogs(pageDto));
    }

    @GetMapping("/unread")
    public ResponseEntity<UnreadCountDto> getUnreadMessagesCount() throws UnreadMessagesCountNotFoundExceptions,
            AccountNotFoundExceptions {
        return ResponseEntity.ok(dialogService.getUnreadMessagesCount());
    }
}
