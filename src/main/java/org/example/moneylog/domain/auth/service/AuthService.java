package org.example.moneylog.domain.auth.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.example.moneylog.common.exception.CustomException;
import org.example.moneylog.common.exception.ErrorCode;
import org.example.moneylog.domain.auth.dto.LoginRequest;
import org.example.moneylog.domain.auth.dto.RefreshRequest;
import org.example.moneylog.domain.auth.dto.SignupRequest;
import org.example.moneylog.domain.auth.dto.SignupResponse;
import org.example.moneylog.domain.auth.dto.TokenResponse;
import org.example.moneylog.domain.auth.entitiy.RefreshToken;
import org.example.moneylog.domain.auth.repository.RefreshTokenRepository;
import org.example.moneylog.domain.category.service.CategoriesService;
import org.example.moneylog.domain.user.entitiy.User;
import org.example.moneylog.domain.user.repository.UserRepository;
import org.example.moneylog.global.security.JwtProvider;
import org.example.moneylog.global.security.TokenHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CategoriesService categoriesService;   // 기본 카테고리 시드용
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입: 비밀번호는 반드시 인코딩해서 저장
    @Transactional
    public SignupResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL); // 409 → 전역 핸들러가 형식으로
        }
        User user = User.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password())) // 평문 저장 금지!
                .nickname(req.nickname())
                .build();
        userRepository.save(user);

        // 회원가입 시 기본 카테고리 자동 생성 (식비/교통/주거/문화, 급여/용돈)
        categoriesService.seedDefaultCategories(user.getId());

        return SignupResponse.from(user);
    }

    // 로그인: 검증 성공 시에만 accessToken + refreshToken 발급
    @Transactional
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 입력 비번과 저장된 해시를 비교 (평문 비교 아님!)
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS); // 이메일/비번 구분 노출 안 함
        }

        String accessToken = jwtProvider.createToken(user.getId(), user.getEmail());
        String refreshToken = issueRefreshToken(user.getId());
        return TokenResponse.of(accessToken, refreshToken, user.getNickname());
    }

    // 토큰 재발급: refreshToken 서명/만료/DB 존재 여부를 검증한 뒤 회전(기존 폐기 + 신규 발급)
    @Transactional
    public TokenResponse reissue(RefreshRequest req) {
        Long userId = validateRefreshToken(req.refreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.deleteByUserId(userId);   // 재사용 방지를 위한 회전(rotation)

        String accessToken = jwtProvider.createToken(user.getId(), user.getEmail());
        String refreshToken = issueRefreshToken(user.getId());
        return TokenResponse.of(accessToken, refreshToken, user.getNickname());
    }

    // 로그아웃: 서버에 저장된 refreshToken을 폐기해 재발급을 막는다
    @Transactional
    public void logout(RefreshRequest req) {
        Long userId = validateRefreshToken(req.refreshToken());
        refreshTokenRepository.deleteByUserId(userId);
    }

    private Long validateRefreshToken(String refreshToken) {
        Long userId;
        try {
            userId = jwtProvider.parseRefreshTokenUserId(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken saved = refreshTokenRepository.findByTokenHash(TokenHasher.sha256(refreshToken))
                .filter(rt -> rt.getUserId().equals(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
        if (saved.isExpired()) {
            refreshTokenRepository.deleteByUserId(userId);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return userId;
    }

    // refreshToken을 생성하고 해시로 저장(원문은 클라이언트에만 전달)
    private String issueRefreshToken(Long userId) {
        String refreshToken = jwtProvider.createRefreshToken(userId);
        RefreshToken entity = RefreshToken.builder()
                .userId(userId)
                .tokenHash(TokenHasher.sha256(refreshToken))
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtProvider.refreshExpirationMs())))
                .build();
        refreshTokenRepository.save(entity);
        return refreshToken;
    }
}
