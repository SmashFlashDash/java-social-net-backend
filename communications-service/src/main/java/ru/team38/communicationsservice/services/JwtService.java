package ru.team38.communicationsservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getUsernameFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            try {
                Jws<Claims> jws = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                        .build()
                        .parseClaimsJws(token);

                return jws.getBody().getSubject();
            } catch (ExpiredJwtException e) {
                SecurityContextHolder.clearContext();
            }
        }
        return null;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Optional<Cookie> tokenCookie = Arrays.stream(cookies)
                        .filter(cookie -> "token".equals(cookie.getName()))
                        .findFirst();

                if (tokenCookie.isPresent()) {
                    return tokenCookie.get().getValue();
                }
            }
        }
        return null;
    }
}