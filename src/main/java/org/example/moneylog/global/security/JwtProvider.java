package org.example.moneylog.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Component
public class JwtProvider {

    private static final String TYPE_CLAIM = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final long expirationMs;
    private final long refreshExpirationMs;

    // 시크릿·만료시간은 JwtProperties(=application.yml → jwt.*)에서 주입 — 하드코딩/랜덤생성 금지
    public JwtProvider(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(decodeSecret(jwtProperties.secret()));
        this.expirationMs = jwtProperties.expirationMs();
        this.refreshExpirationMs = jwtProperties.refreshExpirationMs();
    }

    // 시크릿이 표준 Base64('+','/')든 URL-safe Base64('-','_')든 모두 허용
    private static byte[] decodeSecret(String secret) {
        try {
            return Decoders.BASE64.decode(secret);
        } catch (DecodingException e) {
            return Decoders.BASE64URL.decode(secret);
        }
    }

    // userId를 subject에, email을 클레임에 담아 서명 (accessToken)
    public String createToken(Long userId, String email) {
        return build(userId, expirationMs, TYPE_ACCESS, claims -> claims.claim("email", email));
    }

    // refreshToken — subject(userId)와 만료시간만 담아 서명. accessToken보다 수명이 길다.
    public String createRefreshToken(Long userId) {
        return build(userId, refreshExpirationMs, TYPE_REFRESH, claims -> {});
    }

    // 서명·만료 검증 후 userId 추출. accessToken이 아니면 예외 (refreshToken 도용 방지)
    public Long parseUserId(String token) throws JwtException, IllegalArgumentException {
        return parseUserId(token, TYPE_ACCESS);
    }

    // refreshToken 검증 후 userId 추출. accessToken이 넘어오면 예외 (accessToken 도용 방지)
    public Long parseRefreshTokenUserId(String token) throws JwtException, IllegalArgumentException {
        return parseUserId(token, TYPE_REFRESH);
    }

    public long refreshExpirationMs() {
        return refreshExpirationMs;
    }

    private Long parseUserId(String token, String expectedType) throws JwtException, IllegalArgumentException {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (!expectedType.equals(claims.get(TYPE_CLAIM, String.class))) {
            throw new JwtException("잘못된 토큰 종류입니다.");
        }
        return Long.valueOf(claims.getSubject());
    }

    private String build(Long userId, long ttlMs, String type, Consumer<JwtBuilder> customizer) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);
        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .id(UUID.randomUUID().toString())   // jti — 같은 초에 발급돼도 토큰이 유일하도록 보장
                .claim(TYPE_CLAIM, type)
                .issuedAt(now)
                .expiration(expiry);
        customizer.accept(builder);
        return builder.signWith(key).compact();
    }
}
