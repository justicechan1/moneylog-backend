package org.example.moneylog.domain.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.example.moneylog.domain.category.entitiy.CategoryType;

@Schema(description = "거래내역 등록/수정 요청")
public record TransactionRequest(
        @Schema(description = "거래 타입(수입/지출)")
        @NotNull(message = "거래 타입은 필수입니다.")
        CategoryType type,          // INCOME / EXPENSE

        @Schema(description = "금액(원 단위)", example = "15000")
        @NotNull(message = "금액은 필수입니다.")
        @Positive(message = "금액은 0보다 커야 합니다.")
        Long amount,                   // 원 단위

        @Schema(description = "카테고리 ID", example = "1")
        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryId,

        @Schema(description = "설명(최대 200자)", example = "점심 식사")
        @Size(max = 200, message = "설명은 200자 이하여야 합니다.")
        String description,

        @Schema(description = "거래일", example = "2026-07-27")
        @NotNull(message = "거래일은 필수입니다.")
        LocalDate transactionDate
) {}