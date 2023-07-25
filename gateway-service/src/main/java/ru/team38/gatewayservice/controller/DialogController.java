package ru.team38.gatewayservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.team38.common.dto.dialog.PageDialogDto;
import ru.team38.common.dto.dialog.UnreadCountDto;
import ru.team38.gatewayservice.service.DialogService;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogController {
    private final DialogService dialogService;
    @GetMapping()
    public PageDialogDto getDialogs(@RequestParam(value = "page") Integer page,
                                    @RequestParam(value = "size", required = false, defaultValue = "20")
                                    Integer size,
                                    @RequestParam(value = "sort", required = false) List<String> sort){
        log.info("Executing getDialogs request");
        return dialogService.getDialogs(page, size, sort);
    }

    @GetMapping("/unread")
    public UnreadCountDto getUnreadMessagesCount(){
        log.info("Executing getUnreadMessagesCount request");
        return dialogService.getUnreadMessagesCount();
    }
}
