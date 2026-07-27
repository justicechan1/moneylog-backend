package org.example.moneylog.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "월별 통계 응답")
public record StatisticsResponse(
        @Schema(description = "수입 합계", example = "500000") long income,
        @Schema(description = "지출 합계", example = "300000") long expense,
        @Schema(description = "잔액(수입-지출)", example = "200000") long balance,
        @Schema(description = "카테고리별 합계") List<CategorySum> byCategory
) {
    @Schema(description = "카테고리별 합계")
    public record CategorySum(
            @Schema(description = "카테고리명", example = "식비") String categoryName,
            @Schema(description = "합계", example = "150000") long total) {}

    public static StatisticsResponse of(long income, long expense, List<CategorySum> byCategory) {
        return new StatisticsResponse(income, expense, income - expense, byCategory);
    }
}
