package ru.team38.communicationsservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.aspects.LoggingMethod;
import ru.team38.common.dto.like.CreateLikeDto;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.common.dto.notification.NotificationTypeEnum;
import ru.team38.common.dto.other.PublicationType;
import ru.team38.common.services.NotificationAddService;
import ru.team38.communicationsservice.data.repositories.LikeRepository;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final JwtService jwtService;
    private final LikeRepository likeRepository;
    private final NotificationAddService notificationService;

    @LoggingMethod
    public LikeDto getPostLike(HttpServletRequest request, CreateLikeDto createLikeDto, UUID itemId) {
        LikeDto likeDto = likeDtoBuilder(request, createLikeDto, itemId);
        likeDto = likeRepository.getLikeByPost(likeDto);
        notificationService.addNotification(likeDto.getAuthorId(), likeDto, NotificationTypeEnum.LIKE);
        return likeDto;
    }

    @LoggingMethod
    public LikeDto getCommentLike(HttpServletRequest request, UUID itemId) {
        LikeDto likeDto = likeDtoBuilder(request,itemId);
        likeDto = likeRepository.getLikeByComment(likeDto);
        notificationService.addNotification(likeDto.getAuthorId(), likeDto, NotificationTypeEnum.LIKE);
        return likeDto;
    }

    @LoggingMethod
    public void deleteLike(HttpServletRequest request, UUID itemId) {
        LikeDto likeDto = likeDtoBuilder(request,itemId);
        likeRepository.deleteLike(likeDto);
    }
    private LikeDto likeDtoBuilder(HttpServletRequest request, CreateLikeDto createLikeDto, UUID itemId){
        String emailUser = jwtService.getUsernameFromToken(request);
        UUID authorId = likeRepository.emailUser2idUser(emailUser);
        PublicationType publicationType = createLikeDto.getReactionType() == null ? PublicationType.COMMENT : PublicationType.POST;
            return LikeDto.builder()
                    .time(ZonedDateTime.now())
                    .id(UUID.randomUUID())
                    .type(publicationType)
                    .isDeleted(false)
                    .reactionType(createLikeDto.getReactionType())
                    .authorId(authorId)
                    .itemId(itemId)
                    .build();

    }
    private LikeDto likeDtoBuilder(HttpServletRequest request, UUID itemId){
        return likeDtoBuilder(request, new CreateLikeDto(), itemId);
    }
}
