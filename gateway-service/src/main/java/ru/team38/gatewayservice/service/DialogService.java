package ru.team38.gatewayservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.other.CountDto;
import ru.team38.common.dto.other.PageResponseDto;
import ru.team38.gatewayservice.clients.CommunicationsServiceClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService {
    private final CommunicationsServiceClient communicationsServiceClient;
    public PageResponseDto<DialogDto> getDialogs(Integer page, Integer size, List<String> sort) {
        try {
            ResponseEntity<PageResponseDto<DialogDto>> responseEntity = communicationsServiceClient.getDialogs(page, size, sort);
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }

    public CountDto getUnreadMessagesCount() {
        try {
            ResponseEntity<CountDto> responseEntity = communicationsServiceClient.getUnreadMessagesCount();
            return responseEntity.getBody();
        } catch (FeignException e) {
            log.error(e.contentUTF8());
            throw new RuntimeException(e.contentUTF8(), e);
        }
    }
}
