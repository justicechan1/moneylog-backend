package org.example.moneylog.domain.transaction.dto;

import org.example.moneylog.domain.category.entitiy.CategoryType;

import java.time.LocalDate;

// 거래 검색 복합 필터 — 전부 선택 조건이며 서로 조합 가능
public record TransactionSearchRequest(
        String keyword,        // description LIKE 검색어
        LocalDate startDate,   // 기간 시작(포함)
        LocalDate endDate,     // 기간 종료(포함)
        Long minAmount,        // 금액 하한(포함)
        Long maxAmount,        // 금액 상한(포함)
        CategoryType type,
        Long categoryId
) {
}
