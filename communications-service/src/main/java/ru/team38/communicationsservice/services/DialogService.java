package ru.team38.communicationsservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.dialog.MessageShortDto;
import ru.team38.common.dto.other.*;
import ru.team38.communicationsservice.data.repositories.DialogRepository;
import ru.team38.communicationsservice.exceptions.AccountNotFoundExceptions;
import ru.team38.communicationsservice.exceptions.UnreadMessagesCountNotFoundExceptions;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService {
    private final DialogRepository dialogRepository;
    private final JwtService jwtService;
    @Autowired
    private final HttpServletRequest request;

    @LoggingMethod
    public Page<DialogDto> getDialogs(Pageable pageable) throws AccountNotFoundExceptions {
        String emailUser = jwtService.getUsernameFromToken(request);
        List<DialogDto> listDialogs = dialogRepository.getDialogs(pageable, emailUser);
        return new PageImpl<>(listDialogs);
    }

    @LoggingMethod
    public CountDto getUnreadMessagesCount() throws UnreadMessagesCountNotFoundExceptions, AccountNotFoundExceptions {
        String emailUser = jwtService.getUsernameFromToken(request);
        if (emailUser.trim().length() == 0) {
            return new CountDto(0);
        }
        Integer unreadMessagesCount = dialogRepository.getUnreadMessagesCount(emailUser);
        return new CountDto(unreadMessagesCount);
    }

    @LoggingMethod
    public DialogDto getDialogByRecipientId(UUID recipientId) throws AccountNotFoundExceptions {
        String emailUser = jwtService.getUsernameFromToken(request);
        return dialogRepository.getDialogDtoByRecipientId(recipientId, emailUser);
    }

    @LoggingMethod
    public Page<MessageShortDto> getMessages(UUID recipientId, Pageable pageable) throws AccountNotFoundExceptions {
        String emailUser = jwtService.getUsernameFromToken(request);
        List<MessageShortDto> messageShortDtoList = dialogRepository
                .getMessages(recipientId, pageable, emailUser);
        return new PageImpl<>(messageShortDtoList);
    }
}