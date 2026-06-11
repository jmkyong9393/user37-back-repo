package com.aivle.bookapp.util;

import com.aivle.bookapp.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public String createAccessToken(User user) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .subject(user.getUserId())
                .claim("type", "access")
                .claim("nickname", user.getNickname())
                .claim("email", user.getEmail())
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String createRefreshToken(User user) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(user.getUserId())
                .claim("type", "refresh")
                .claim("nickname", user.getNickname())
                .claim("email", user.getEmail())
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(getSigningKey())
                .compact();
    }

    // 토큰에서 userId 꺼내기
    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getClaims(token).get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getClaims(token).get("type", String.class));
    }

    // 토큰 내용 파싱
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    // 서명 키 생성
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰으로 부터 닉네임을 가져옴
    public String getNicknameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("nickname", String.class);
    }
}
