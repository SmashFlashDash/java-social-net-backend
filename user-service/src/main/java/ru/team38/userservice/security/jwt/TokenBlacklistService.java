package ru.team38.userservice.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.team38.common.dto.TokensDto;
import ru.team38.userservice.data.repositories.TokenRepository;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenRepository tokenRepository;

    public void addTokenToBlacklist(String token) {
        TokensDto tokenDto = tokenRepository.findByToken(token);
        if (tokenDto != null) {
            tokenDto.setIsValid(false);
            tokenRepository.update(tokenDto);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        TokensDto tokenDto = tokenRepository.findByToken(token);
        return tokenDto != null && !tokenDto.getIsValid();
    }
}