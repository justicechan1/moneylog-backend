package org.example.moneylog.domain.transaction.dto;

import org.example.moneylog.domain.category.entitiy.CategoryType;

public record TransactionSearchCond(
        String yearMonth,      // "2026-07"
        CategoryType type,     // 선택 필터: INCOME / EXPENSE
        Long categoryId        // 선택 필터
) {
}