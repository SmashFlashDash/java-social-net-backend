package ru.team38.userservice.services;

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
import ru.team38.userservice.exceptions.status.BadRequestException;
import ru.team38.userservice.exceptions.status.UnauthorizedException;
import ru.team38.userservice.security.jwt.JwtService;
import ru.team38.userservice.security.jwt.TokenBlacklistService;

import java.time.LocalDate;
import java.time.ZonedDateTime;

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

    public LoginResponse login(LoginForm loginForm) throws UsernameNotFoundException, BadCredentialsException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginForm.getEmail());
        boolean isValidPassword = BCrypt.checkpw(loginForm.getPassword(), userDetails.getPassword());
        if (!isValidPassword) {
            throw new BadCredentialsException("Invalid password");
        }

        String accessToken = jwtService.createAccessToken(userDetails);
        String refreshToken = jwtService.createRefreshToken(userDetails);

        TokensDto accessTokenDto = new TokensDto(null, accountRepository.getIdByEmail(loginForm.getEmail()), "access", accessToken, true);
        TokensDto refreshTokenDto = new TokensDto(null, accountRepository.getIdByEmail(loginForm.getEmail()), "refresh", refreshToken, true);

        tokenRepository.save(accessTokenDto);
        tokenRepository.save(refreshTokenDto);

        return new LoginResponse(accessToken, refreshToken);
    }

    public void logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            tokenBlacklistService.addTokenToBlacklist(token);
        }
        SecurityContextHolder.clearContext();
    }
}