package ru.team38.userservice.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("#{${jwt.token-validity-min}*60*1000}")
    private long accessTokenExpiration;

    @Value("#{${jwt.refresh-token-validity}*24*60*60*1000}")
    private long refreshTokenExpiration;
    private final TokenBlacklistService tokenBlacklistService;

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token) && !tokenBlacklistService.isTokenBlacklisted(token);
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired (String token) {
        return getClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
    }

    public String createAccessToken(UserDetails userDetails, String deviceUUID) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("deviceUUID", deviceUUID)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(UserDetails userDetails, String deviceUUID) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("deviceUUID", deviceUUID)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getDeviceUUID(String token) {
        return getClaim(token, claims -> claims.get("deviceUUID", String.class));
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}