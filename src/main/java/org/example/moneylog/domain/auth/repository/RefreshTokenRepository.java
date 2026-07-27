package org.example.moneylog.domain.auth.repository;

import org.example.moneylog.domain.auth.entitiy.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUserId(Long userId);   // 로그아웃/재발급 시 기존 토큰 폐기(회전)
}
