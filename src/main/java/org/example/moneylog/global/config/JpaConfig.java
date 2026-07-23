package org.example.moneylog.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // 저장/수정 시각을 자동으로 기록해주는 스위치 역할( createdAt/updatedAt )
public class JpaConfig {
}
