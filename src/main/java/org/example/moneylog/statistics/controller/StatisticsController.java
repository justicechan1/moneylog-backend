package org.example.moneylog.statistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.moneylog.common.response.ApiResponse;
import org.example.moneylog.statistics.dto.MonthlyTrendResponse;
import org.example.moneylog.statistics.dto.StatisticsResponse;
import org.example.moneylog.statistics.service.StatisticsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "통계", description = "월별 수입/지출 통계 및 추이 조회 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "월별 통계 조회", description = "지정한 연월(yyyy-MM)의 수입/지출 합계와 카테고리별 통계를 조회합니다.")
    @GetMapping("/monthly")
    public ApiResponse<StatisticsResponse> monthly(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "조회할 연월", example = "2026-07") @RequestParam String yearMonth) {
        return ApiResponse.success("월별 통계 조회에 성공했습니다.",
                statisticsService.monthly(userId, yearMonth));
    }

    @Operation(summary = "월별 추이 조회", description = "최근 N개월(기본 6개월)간의 수입/지출 추이를 조회합니다. 프론트엔드 차트에 사용됩니다.")
    @GetMapping("/trend")
    public ApiResponse<MonthlyTrendResponse> trend(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "조회할 개월 수", example = "6") @RequestParam(defaultValue = "6") int months) {
        return ApiResponse.success("월별 추이 조회에 성공했습니다.",
                statisticsService.trend(userId, months));
    }
}
