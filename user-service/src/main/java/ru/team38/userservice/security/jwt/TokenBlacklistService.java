package ru.team38.userservice.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.dto.account.TokensDto;
import ru.team38.userservice.data.repositories.TokenRepository;

import java.util.List;

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

    @Scheduled(cron = "0 0 0 * * MON", zone = "UTC")
    @Transactional
    public void deleteInvalidTokensWeekly() {
        tokenRepository.deleteInvalidTokens();
    }

    @Scheduled(cron = "0 0 0 1 * ?", zone = "UTC")
    @Transactional
    public void deleteExpiredTokensMonthly() {
        tokenRepository.deleteExpiredTokens();
    }

    @Transactional
    public void addTokensToBlacklistByDeviceUUID(String deviceUUID) {
        List<TokensDto> tokens = tokenRepository.findByDeviceUUID(deviceUUID);
        for (TokensDto token : tokens) {
            token.setIsValid(false);
            tokenRepository.update(token);
        }
    }
}
