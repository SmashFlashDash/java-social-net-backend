package ru.team38.userservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.team38.common.dto.*;
import ru.team38.userservice.data.repositories.AccountRepository;
import ru.team38.userservice.data.repositories.TokenRepository;
import ru.team38.userservice.exceptions.InvalidTokenException;
import ru.team38.userservice.exceptions.status.BadRequestException;
import ru.team38.userservice.exceptions.status.UnauthorizedException;
import ru.team38.userservice.security.jwt.JwtService;
import ru.team38.userservice.security.jwt.TokenBlacklistService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final AccountRepository accountRepository;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenRepository tokenRepository;

    @Transactional
    public void register(RegisterDto registerDto) {
        if (!registerDto.getPassword1().equals(registerDto.getPassword2())) {
            throw new BadRequestException("Passwords mismatch");
        }
        accountRepository.getAccountByEmail(registerDto.getEmail()).ifPresent(rec -> {
            throw new UnauthorizedException("User exists");
        });
        AccountDto newAccount = AccountDto.builder().email(registerDto.getEmail())
                .password(encoder.encode(registerDto.getPassword1()))
                .firstName(registerDto.getFirstName()).lastName(registerDto.getLastName())
                .isDeleted(false).isOnline(false).isBlocked(false).createdOn(ZonedDateTime.now())
                .messagePermission(true).regDate(ZonedDateTime.now()).birthDate(LocalDate.now())
                .build();
        accountRepository.save(newAccount);
    }

    public LoginResponse login(HttpServletRequest request, LoginForm loginForm) throws UsernameNotFoundException, BadCredentialsException {
        UserDetails userDetails = authenticateUser(loginForm);
        String deviceUUID = generateDeviceUUID(request);
        String accessToken = jwtService.createAccessToken(userDetails, deviceUUID);
        String refreshToken = jwtService.createRefreshToken(userDetails, deviceUUID);
        saveToken(loginForm.getEmail(), accessToken, "access", deviceUUID);
        saveToken(loginForm.getEmail(), refreshToken, "refresh", deviceUUID);
        return new LoginResponse(accessToken, refreshToken);
    }

    private UserDetails authenticateUser(LoginForm loginForm) throws UsernameNotFoundException, BadCredentialsException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginForm.getEmail());
        boolean isValidPassword = BCrypt.checkpw(loginForm.getPassword(), userDetails.getPassword());
        if (!isValidPassword) {
            throw new BadCredentialsException("Invalid password");
        }
        return userDetails;
    }

    private String generateDeviceUUID(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String info = ip + userAgent;
        return UUID.nameUUIDFromBytes(info.getBytes()).toString();
    }

    private void saveToken(String username, String token, String type, String deviceUUID) {
        ZonedDateTime tokenExpiration =
                ZonedDateTime.ofInstant(jwtService.getClaim(token, Claims::getExpiration).toInstant(), ZoneId.systemDefault());
        Long accountId = accountRepository.getIdByEmail(username);
        TokensDto tokenDto =
                new TokensDto(null, accountId, type, token, true, tokenExpiration, deviceUUID);
        tokenRepository.save(tokenDto);
    }

    public void logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String deviceUUID = jwtService.getDeviceUUID(token);
            tokenBlacklistService.addTokensToBlacklistByDeviceUUID(deviceUUID);
        }
        SecurityContextHolder.clearContext();
    }

    public LoginResponse refreshAccessToken(String refreshToken) throws InvalidTokenException {
        String username;
        try {
            username = jwtService.getUsername(refreshToken);
        } catch (ExpiredJwtException e) {
            tokenBlacklistService.addTokenToBlacklist(refreshToken);
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
        String deviceUUID = jwtService.getDeviceUUID(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtService.createAccessToken(userDetails, deviceUUID);
        saveToken(username, newAccessToken, "access", deviceUUID);
        return new LoginResponse(newAccessToken, refreshToken);
    }
}
