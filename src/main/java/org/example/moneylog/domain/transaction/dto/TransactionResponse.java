package org.example.moneylog.domain.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.moneylog.domain.category.entitiy.CategoryType;
import org.example.moneylog.domain.transaction.entitiy.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "거래내역 응답")
public record TransactionResponse(
        @Schema(description = "거래내역 ID", example = "1") Long id,
        @Schema(description = "거래 타입(수입/지출)") CategoryType type,
        @Schema(description = "금액(원 단위)", example = "15000") Long amount,
        @Schema(description = "카테고리 ID", example = "1") Long categoryId,
        @Schema(description = "카테고리명", example = "식비") String categoryName,           // 화면 편의를 위해 카테고리명 포함
        @Schema(description = "설명", example = "점심 식사") String description,
        @Schema(description = "거래일", example = "2026-07-27") LocalDate transactionDate,
        @Schema(description = "등록일시") LocalDateTime createdAt
) {
    // 엔티티 → 응답 DTO 변환 정적 팩토리
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getAmount(),
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getDescription(),
                t.getTransactionDate(),
                t.getCreatedAt()
        );
    }
}

