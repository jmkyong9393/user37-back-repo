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

    @Value("${jwt.expiration}")
    private Long expiration;

    // 토큰 생성
    public String createToken(User user) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getUserId())
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
}
