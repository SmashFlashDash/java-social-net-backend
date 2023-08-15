package ru.team38.common.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.comment.CommentDto;
import ru.team38.common.dto.friend.FriendShortDto;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.common.dto.notification.NotificationDto;
import ru.team38.common.dto.notification.NotificationTypeEnum;
import ru.team38.common.dto.other.PublicationType;
import ru.team38.common.dto.post.PostDto;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.common.repository.*;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationAddService {
    private final PostCommonRepository postRepository;
    private final CommentCommonRepository commentRepository;
    private final NotificationCommonRepository notificationRepository;
    private final FriendCommonRepository friendRepository;
    private final AccountCommonRepository accountRepository;


    @LoggingMethod
    @Transactional
    public void addNotification(UUID authorID, Object referenceDto, NotificationTypeEnum ntfType) {
        try {
            List<NotificationDto> notifications = switch (ntfType) {
                case POST -> makeNotificationPOST(authorID, (PostDto) referenceDto);
                case FRIEND_REQUEST -> makeNotificationFRIEND(authorID, (FriendShortDto) referenceDto);     //TODO:
                case LIKE -> makeNotificationLIKE(authorID, (LikeDto) referenceDto);
                case MESSAGE -> Collections.emptyList();           // TODO:
                case POST_COMMENT, COMMENT_COMMENT -> makeNotificationCOMMENT(authorID, (CommentDto) referenceDto, ntfType);
                case SEND_EMAIL_MESSAGE -> Collections.emptyList(); // TODO:
                default -> {
                    log.error("Disabled add notification with NotificationTypeEnum: " + ntfType.name());
                    yield Collections.emptyList();
                }
            };
            saveNotifications(notifications);
        } catch (RuntimeException ex) {
            log.error("Error add notification type: " + ntfType.name());
            ex.printStackTrace();
        }

    }

    @LoggingMethod
    @Transactional
    @Scheduled(cron = "${jobs.addNotificationBirthday.cron}")
    public void addNotificationBirthday() {
        List<AccountRecord> accountsBirthday = accountRepository.findAccountsByCurrentDate();
        accountsBirthday = accountsBirthday.stream().filter(account ->
                isStartTimeByCityAndCountry(account.getBirthDate(), account.getCity(), account.getCountry())).toList();
        List<NotificationDto> notifications = accountsBirthday.stream().map(account -> {
            List<AccountRecord> friends = friendRepository.getFriendAccountsListByAccountId(account.getId());
            return friends.stream().filter(AccountRecord::getEnableFriendBirthday)
                    .map(friendAccount -> NotificationDto.builder()
                            .authorId(account.getId())
                            .receiverId(friendAccount.getId())
                            .notificationType(NotificationTypeEnum.FRIEND_BIRTHDAY)
                            .sendTime(ZonedDateTime.now())
                            .isReaded(false)
                            .build())
                    .toList();
        }).flatMap(List::stream).toList();
        saveNotifications(notifications);
    }

    private List<NotificationDto> makeNotificationPOST(UUID authorID, PostDto post) {
        List<AccountRecord> friendAccounts = friendRepository.getFriendAccountsListByAccountId(post.getAuthorId());
        return friendAccounts.stream()
                .filter(AccountRecord::getEnablePost)
                .map(friend -> buildNotification(authorID, friend.getId(), post.getTitle(), NotificationTypeEnum.POST))
                .toList();
    }

    private List<NotificationDto> makeNotificationFRIEND(UUID authorID, FriendShortDto referenceDto) {
        return switch (referenceDto.getStatusCode()) {
            case FRIEND -> List.of(buildNotification(authorID, referenceDto.getIdFriend(),
                    "Принято предложение дружбы", NotificationTypeEnum.FRIEND_REQUEST));
            case REQUEST_FROM -> List.of(buildNotification(authorID, referenceDto.getIdFriend(),
                    "Предлагает дружбу", NotificationTypeEnum.FRIEND_REQUEST));
            default -> {
                log.error("Disabled add notification with NotificationTypeEnum: " + NotificationTypeEnum.FRIEND_REQUEST + "with status" + referenceDto.getStatusCode());
                yield Collections.emptyList();
            }
        };
    }

    private List<NotificationDto> makeNotificationCOMMENT(UUID authorID, CommentDto comment, NotificationTypeEnum type) {
        PublicationType commentType = comment.getCommentType();
        UUID commentItemAuthorId = commentType == PublicationType.POST ?
                postRepository.getPostDtoById(comment.getPostId()).getAuthorId() :
                commentRepository.getCommentById(comment.getParentId()).getAuthorId();
        if (authorID.equals(commentItemAuthorId)) {
            return Collections.emptyList();
        }
        return List.of(buildNotification(authorID, commentItemAuthorId, comment.getCommentText(), type));
    }

    private List<NotificationDto> makeNotificationLIKE(UUID authorID, LikeDto likeDto) {
        AccountRecord author;
        NotificationDto ntf = switch (likeDto.getType()) {
            case POST:
                PostDto post = postRepository.getPostDtoById(likeDto.getItemId());
                author = accountRepository.findAccountByID(post.getAuthorId()).orElse(null);
                yield author != null && author.getEnableLike() && !author.getId().equals(authorID) ?
                        buildNotification(authorID, post.getAuthorId(), post.getTitle(), NotificationTypeEnum.LIKE) : null;
            case COMMENT:
                CommentDto comment = commentRepository.getCommentById(likeDto.getItemId());
                author = accountRepository.findAccountByID(comment.getAuthorId()).orElse(null);
                yield author != null && author.getEnableLike() && !author.getId().equals(authorID) ?
                        buildNotification(authorID, comment.getAuthorId(), comment.getCommentText(), NotificationTypeEnum.LIKE) : null;
            default: {
                log.error("Disabled add notification with LikeDto.type(): " + likeDto.getType());
                yield null;
            }
        };
        return ntf == null ? Collections.emptyList() : List.of(ntf);
    }

    private NotificationDto buildNotification(UUID authorId, UUID receiverId, String content, NotificationTypeEnum type) {
        return NotificationDto.builder()
                .authorId(authorId)
                .receiverId(receiverId)
                .notificationType(type)
                .content(trimTextByLength(content))
                .sendTime(ZonedDateTime.now()).isReaded(false).build();
    }

    private void saveNotifications(List<NotificationDto> notifications) {
        if (notifications.size() > 1) {
            notificationRepository.addNotificationsBatch(notifications);
        } else if (notifications.size() == 1) {
            notificationRepository.addNotification(notifications.get(0));
        }
    }

    private String trimTextByLength(String text) {
        final int TEXT_PREVIEW_LENGTH = 50;
        final int TEXT_MIN_LENGTH = 20;
        if (text.length() <= TEXT_PREVIEW_LENGTH) {
            return text;
        }
        text = text.substring(0, TEXT_PREVIEW_LENGTH);
        int pos = text.lastIndexOf(" ");
        text = text.substring(0, pos >= TEXT_MIN_LENGTH ? pos : TEXT_PREVIEW_LENGTH).trim();
        return text.concat("...");
    }
    private boolean isStartTimeByCityAndCountry(LocalDate birthdayDate, String city, String country) {
        LocalTime startTime = LocalTime.of(0, 0);
        LocalTime endTime = LocalTime.of(0, 59);

        ZoneId zoneId = accountRepository.getZoneIdByCityAndCountry(city, country);
        boolean isCurrentDate = LocalDate.now(zoneId).getDayOfMonth() == birthdayDate.getDayOfMonth();
        LocalTime currentTime = LocalTime.now(zoneId);

        return isCurrentDate && currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }
}
