package org.example.moneylog.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 독립된 테이블로 만들지 않고 공통 필드만 제공
@EntityListeners(AuditingEntityListener.class)  //엔티티의 생성/수정 이벤트를 감지
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;  //데이터 생성 일시

    @LastModifiedDate
    private LocalDateTime updatedAt;  // 데이터 최종 수정 일시

}
