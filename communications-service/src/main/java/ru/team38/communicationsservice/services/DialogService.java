package ru.team38.communicationsservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.PageDto;
import ru.team38.common.dto.PageableObject;
import ru.team38.common.dto.Sort;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.dialog.PageDialogDto;
import ru.team38.common.dto.dialog.UnreadCountDto;
import ru.team38.communicationsservice.data.repositories.DialogRepository;
import ru.team38.communicationsservice.exceptions.AccountNotFoundExceptions;
import ru.team38.communicationsservice.exceptions.UnreadMessagesCountNotFoundExceptions;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService {
    private final DialogRepository dialogRepository;
    private final JwtService jwtService;
    @Autowired
    private final HttpServletRequest request;

    public PageDialogDto getDialogs(PageDto pageDto) throws AccountNotFoundExceptions {
        try {
            String emailUser = jwtService.getUsernameFromToken(request);
            List<DialogDto> listDialogs = dialogRepository.getListDialogs(pageDto, emailUser);
            int pageSize = pageDto.getSize();
            Integer pageNumber = pageDto.getPage();
            int offset = (pageNumber != null) ? pageNumber * pageSize : 0;
            int totalDialogCount = listDialogs.size();
            int totalPages = totalDialogCount / pageSize;
            if (totalDialogCount % pageSize > 0) {
                totalPages += 1;
            }
            Sort sort = new Sort(false, true, false);
            PageableObject pageableObject = new PageableObject(offset, sort, pageSize, true, false, pageNumber);
            return new PageDialogDto(totalPages, listDialogs.size(), sort, listDialogs.size(),
                    pageableObject, true, true, pageDto.getSize(), listDialogs, totalDialogCount, false);
        } catch (Exception e) {
            log.error("Error occurred while retrieving dialogs: {}", e.getMessage());
            throw e;
        }
    }

    public UnreadCountDto getUnreadMessagesCount() throws UnreadMessagesCountNotFoundExceptions, AccountNotFoundExceptions {
        try {
            String emailUser = jwtService.getUsernameFromToken(request);
            if (emailUser.trim().length() == 0) {
                return new UnreadCountDto(0);
            }
            Integer unreadMessagesCount = dialogRepository.getAllUnreadMessagesCount(emailUser);
            return new UnreadCountDto(unreadMessagesCount);
        } catch (Exception e) {
            log.error("Error occurred while retrieving count of unread messages: {}", e.getMessage());
            throw e;
        }
    }
}