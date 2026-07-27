package org.example.moneylog.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "월별 추이 응답")
public record MonthlyTrendResponse(@Schema(description = "월별 추이 목록") List<MonthlyPoint> trend) {

    @Schema(description = "월별 수입/지출/잔액")
    public record MonthlyPoint(
            @Schema(description = "연월", example = "2026-07") String yearMonth,
            @Schema(description = "수입 합계", example = "500000") long income,
            @Schema(description = "지출 합계", example = "300000") long expense,
            @Schema(description = "잔액(수입-지출)", example = "200000") long balance) {
        public static MonthlyPoint of(String yearMonth, long income, long expense) {
            return new MonthlyPoint(yearMonth, income, expense, income - expense);
        }
    }
}
