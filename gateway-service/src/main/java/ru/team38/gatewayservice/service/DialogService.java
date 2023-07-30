package ru.team38.gatewayservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.dialog.PageDialogDto;
import ru.team38.common.dto.dialog.UnreadCountDto;
import ru.team38.gatewayservice.clients.CommunicationsServiceClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService {
    private final CommunicationsServiceClient communicationsServiceClient;
    public PageDialogDto getDialogs(Integer page, Integer size, List<String> sort) {
        try {
            ResponseEntity<PageDialogDto> responseEntity = communicationsServiceClient.getDialogs(page, size, sort);
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }

    public UnreadCountDto getUnreadMessagesCount() {
        try {
            ResponseEntity<UnreadCountDto> responseEntity = communicationsServiceClient.getUnreadMessagesCount();
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }
}
