package ru.team38.communicationsservice.data.repositories;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.team38.common.dto.dialog.DialogDto;
import ru.team38.common.dto.dialog.MessageDto;
import ru.team38.common.dto.dialog.MessageShortDto;
import ru.team38.common.dto.dialog.ReadStatusDto;
import ru.team38.common.jooq.tables.Account;
import ru.team38.common.jooq.tables.Message;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.jooq.tables.records.MessageRecord;
import ru.team38.common.mappers.DialogMapper;
import ru.team38.communicationsservice.exceptions.AccountNotFoundExceptions;
import ru.team38.communicationsservice.exceptions.UnreadMessagesCountNotFoundExceptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DialogRepository {
    private final DSLContext dsl;
    private static final Account account = Account.ACCOUNT;
    private static final Message message = Message.MESSAGE;
    private final DialogMapper dialogMapper = Mappers.getMapper(DialogMapper.class);

    public List<DialogDto> getDialogs(Pageable pageable, String emailUser) throws AccountNotFoundExceptions {
        UUID accountId = emailUser2idUser(emailUser);
        List<DialogDto> dialogDtoList = new ArrayList<>();
        List<MessageDto> lastMessagesDto = getLastMessagesDto(accountId);
        if (!lastMessagesDto.isEmpty()) {
            lastMessagesDto.forEach(messageDto ->
                    dialogDtoList.add(new DialogDto(
                            getDialogUnreadMessagesCount(accountId, messageDto.getDialogId()),
                            messageDto.getConversationPartner1(), messageDto.getConversationPartner2(),
                            new ArrayList<>(List.of(messageDto)))));
        }
        if (pageable.getPageSize() == 0) {
            return dialogDtoList;
        }
        String sortField = pageable.getSort().get().toList().get(0).getProperty();
        String sortType = pageable.getSort().get().toList().get(0).getDirection().name();
        if (sortField.equals("unreadCount") && (sortType.equalsIgnoreCase("desc"))) {
            dialogDtoList.sort(Comparator.comparingInt(DialogDto::getUnreadCount).reversed());
        }
        return dialogDtoList;
    }

    private List<MessageDto> getLastMessagesDto(UUID accountId) {
        List<MessageRecord> messageRecords = dsl.select()
                .distinctOn(message.DIALOG_ID)
                .from(message)
                .where(message.CONVERSATION_PARTNER1.eq(accountId)
                        .or(message.CONVERSATION_PARTNER2.eq(accountId)))
                .orderBy(message.DIALOG_ID, DSL.field(message.TIME).sort(SortOrder.DESC))
                .fetch()
                .into(MessageRecord.class);
        return dialogMapper.messageRecordsToMessagesDto(messageRecords);
    }

    private UUID emailUser2idUser(String email) throws AccountNotFoundExceptions {
        AccountRecord accountRecord = dsl.selectFrom(account)
                .where(account.EMAIL.eq(email))
                .fetchOne();
        if (accountRecord == null) {
            throw new AccountNotFoundExceptions();
        }
        return accountRecord.getId();
    }

    public Integer getUnreadMessagesCount(String emailUser) throws AccountNotFoundExceptions, UnreadMessagesCountNotFoundExceptions {
        UUID accountId = emailUser2idUser(emailUser);
        Integer unreadMessagesCount = dsl.selectCount()
                .from(message)
                .where(message.CONVERSATION_PARTNER2.eq(accountId))
                .and(message.READ_STATUS.ne(ReadStatusDto.READ.toString()))
                .fetchOne(0, Integer.class);
        if (unreadMessagesCount == null) {
            throw new UnreadMessagesCountNotFoundExceptions();
        }
        return unreadMessagesCount;
    }

    private Integer getDialogUnreadMessagesCount(UUID accountId, UUID dialogId) {
        return dsl.selectCount()
                .from(message)
                .where(message.CONVERSATION_PARTNER2.eq(accountId))
                .and(message.READ_STATUS.ne(ReadStatusDto.READ.toString()))
                .and(message.DIALOG_ID.eq(dialogId))
                .fetchOne(0, Integer.class);
    }

    public DialogDto getDialogDtoByRecipientId(UUID recipientId, String emailUser) throws AccountNotFoundExceptions {
        UUID accountId = emailUser2idUser(emailUser);
        MessageRecord lastMessageRecord = dsl.selectFrom(message)
                .where(message.CONVERSATION_PARTNER1.eq(accountId).and(message.CONVERSATION_PARTNER2.eq(recipientId))
                        .or(message.CONVERSATION_PARTNER1.eq(recipientId).and(message.CONVERSATION_PARTNER2.eq(accountId))))
                .orderBy(DSL.field(message.TIME).sort(SortOrder.DESC))
                .limit(1)
                .fetchSingle()
                .into(MessageRecord.class);
        UUID dialogId = lastMessageRecord.getDialogId();
        return new DialogDto(
                getDialogUnreadMessagesCount(accountId, dialogId),
                lastMessageRecord.getConversationPartner1(),
                lastMessageRecord.getConversationPartner2(),
                new ArrayList<>(List.of(dialogMapper.messageRecordToMessageDto(lastMessageRecord))));
    }

    public List<MessageShortDto> getMessages(UUID recipientId, Pageable pageable, String emailUser)
            throws AccountNotFoundExceptions {
        UUID accountId = emailUser2idUser(emailUser);
        List<MessageShortDto> messageShortDtoList = getMessagesShortDto(accountId, recipientId);
        if (pageable.getPageSize() == 0 || messageShortDtoList.isEmpty()) {
            return messageShortDtoList;
        }
        String sortField = pageable.getSort().get().toList().get(0).getProperty();
        String sortType = pageable.getSort().get().toList().get(0).getDirection().name();
        if (sortField.equals("time") && (sortType.equalsIgnoreCase("asc"))) {
            messageShortDtoList.sort(Comparator.comparing(MessageShortDto::getTime));
        }
        return messageShortDtoList;
    }

    private List<MessageShortDto> getMessagesShortDto(UUID accountId, UUID recipientId) {
        List<MessageRecord> messageRecords = dsl.selectFrom(message)
                .where(message.CONVERSATION_PARTNER1.eq(accountId).and(message.CONVERSATION_PARTNER2.eq(recipientId))
                        .or(message.CONVERSATION_PARTNER1.eq(recipientId)
                                .and(message.CONVERSATION_PARTNER2.eq(accountId))))
                .fetch()
                .into(MessageRecord.class);
        return dialogMapper.messageRecordsToMessagesShortDto(messageRecords);
    }
}
