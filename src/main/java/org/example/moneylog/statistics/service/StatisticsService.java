package org.example.moneylog.statistics.service;

import lombok.RequiredArgsConstructor;
import org.example.moneylog.domain.category.entitiy.CategoryType;
import org.example.moneylog.statistics.dto.MonthlyTrendResponse;
import org.example.moneylog.statistics.dto.StatisticsResponse;
import org.example.moneylog.statistics.repository.StatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    @Transactional(readOnly = true)
    public StatisticsResponse monthly(Long userId, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        long income = 0, expense = 0;
        for (Object[] row : statisticsRepository.sumByType(userId, start, end)) {
            CategoryType type = (CategoryType) row[0];
            long sum = (long) row[1];
            if (type == CategoryType.INCOME) income = sum;
            else expense = sum;
        }

        var byCategory = statisticsRepository.sumExpenseByCategory(userId, start, end);
        return StatisticsResponse.of(income, expense, byCategory);
    }

    // 최근 N개월(당월 포함) income/expense 추이 — 프론트 차트용
    @Transactional(readOnly = true)
    public MonthlyTrendResponse trend(Long userId, int months) {
        YearMonth current = YearMonth.now();
        YearMonth startMonth = current.minusMonths(months - 1L);
        LocalDate start = startMonth.atDay(1);
        LocalDate end = current.atEndOfMonth();

        // 거래가 없는 달도 0원으로 채워서 응답하기 위해 순서를 미리 만들어 둔다
        Map<YearMonth, long[]> sums = new LinkedHashMap<>();
        for (YearMonth ym = startMonth; !ym.isAfter(current); ym = ym.plusMonths(1)) {
            sums.put(ym, new long[]{0L, 0L});   // [income, expense]
        }

        for (Object[] row : statisticsRepository.findRawForTrend(userId, start, end)) {
            LocalDate date = (LocalDate) row[0];
            CategoryType type = (CategoryType) row[1];
            long amount = (long) row[2];
            long[] pair = sums.get(YearMonth.from(date));
            if (type == CategoryType.INCOME) pair[0] += amount;
            else pair[1] += amount;
        }

        List<MonthlyTrendResponse.MonthlyPoint> points = sums.entrySet().stream()
                .map(e -> MonthlyTrendResponse.MonthlyPoint.of(e.getKey().toString(), e.getValue()[0], e.getValue()[1]))
                .toList();
        return new MonthlyTrendResponse(points);
    }
}
