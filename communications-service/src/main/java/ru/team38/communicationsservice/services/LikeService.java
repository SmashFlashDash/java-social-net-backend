package ru.team38.communicationsservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.dto.like.CreateLikeDto;
import ru.team38.common.dto.like.LikeDto;
import ru.team38.communicationsservice.data.repositories.LikeRepository;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final JwtService jwtService;
    private final LikeRepository likeRepository;
    public LikeDto getPostLike(HttpServletRequest request, CreateLikeDto createLikeDto, UUID itemId) {
        try {
            String emailUser = jwtService.getUsernameFromToken(request);
            return likeRepository.getLikeByReactionTypeAndEmail(createLikeDto.getReactionType(), emailUser, itemId);
        } catch (Exception e) {
            log.error("Error occurred while retrieving post like: {}", e.getMessage());
            throw e;
        }
    }
    public LikeDto getCommentLike(HttpServletRequest request, UUID itemId){
        try {
            String emailUser = jwtService.getUsernameFromToken(request);
            return likeRepository.getLikeByReactionTypeAndEmail(null, emailUser, itemId);
        } catch (Exception e) {
            log.error("Error occurred while retrieving comment like: {}", e.getMessage());
            throw e;
        }
    }
    public void deleteLike(HttpServletRequest request, UUID itemId){
        String emailUser = jwtService.getUsernameFromToken(request);
        likeRepository.deleteLike(itemId, emailUser);
    }
}
