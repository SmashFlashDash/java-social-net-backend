package ru.team38.communicationsservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.other.*;
import ru.team38.communicationsservice.data.repositories.DialogRepository;
import ru.team38.communicationsservice.exceptions.AccountNotFoundExceptions;
import ru.team38.communicationsservice.exceptions.UnreadMessagesCountNotFoundExceptions;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService {
    private final DialogRepository dialogRepository;
    private final JwtService jwtService;
    @Autowired
    private final HttpServletRequest request;

    public PageResponseDto<DialogDto> getDialogs(PageDto pageDto) throws AccountNotFoundExceptions {
        try {
            String emailUser = jwtService.getUsernameFromToken(request);
            List<DialogDto> listDialogs = dialogRepository.getListDialogs(pageDto, emailUser);
            int pageSize = pageDto.getSize();
            int pageNumber = Optional.ofNullable(pageDto.getPage()).orElse(0);
            int offset = pageNumber * pageSize;
            int totalDialogCount = listDialogs.size();
            int totalPages = totalDialogCount / pageSize;
            if (totalDialogCount % pageSize > 0) {
                totalPages += 1;
            }
            SortDto sort = new SortDto(false, true, false);
            PageableDto pageableDto = new PageableDto(sort, pageNumber, pageSize, offset, false, true);
            return PageResponseDto.<DialogDto>builder()
                    .totalPages(totalPages)
                    .totalElements(listDialogs.size())
                    .sort(sort)
                    .numberOfElements(listDialogs.size())
                    .pageable(pageableDto)
                    .first(true)
                    .last(true)
                    .size(pageDto.getSize())
                    .content(listDialogs)
                    .number(totalDialogCount)
                    .empty(false)
                    .build();
        } catch (Exception e) {
            log.error("Error occurred while retrieving dialogs: {}", e.getMessage());
            throw e;
        }
    }

    public CountDto getUnreadMessagesCount() throws UnreadMessagesCountNotFoundExceptions, AccountNotFoundExceptions {
        try {
            String emailUser = jwtService.getUsernameFromToken(request);
            if (emailUser.trim().length() == 0) {
                return new CountDto(0);
            }
            Integer unreadMessagesCount = dialogRepository.getAllUnreadMessagesCount(emailUser);
            return new CountDto(unreadMessagesCount);
        } catch (Exception e) {
            log.error("Error occurred while retrieving count of unread messages: {}", e.getMessage());
            throw e;
        }
    }
}