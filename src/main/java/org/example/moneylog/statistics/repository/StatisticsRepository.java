package org.example.moneylog.statistics.repository;

import org.example.moneylog.domain.transaction.entitiy.Transaction;
import org.example.moneylog.statistics.dto.StatisticsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Transaction, Long> {

    // 타입별 합계 (income/expense를 한 번에)
    @Query("""
            SELECT t.type, COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.transactionDate BETWEEN :start AND :end
            GROUP BY t.type
            """)
    List<Object[]> sumByType(@Param("userId") Long userId,
                             @Param("start") LocalDate start,
                             @Param("end") LocalDate end);

    // 카테고리별 지출 합계 (DTO 프로젝션으로 바로 매핑)
    @Query("""
            SELECT new org.example.moneylog.statistics.dto.StatisticsResponse$CategorySum(c.name, SUM(t.amount))
            FROM Transaction t JOIN t.category c
            WHERE t.user.id = :userId
              AND t.type = org.example.moneylog.domain.category.entitiy.CategoryType.EXPENSE
              AND t.transactionDate BETWEEN :start AND :end
            GROUP BY c.id, c.name
            ORDER BY SUM(t.amount) DESC
            """)
    List<StatisticsResponse.CategorySum> sumExpenseByCategory(@Param("userId") Long userId,
                                                              @Param("start") LocalDate start,
                                                              @Param("end") LocalDate end);

    // 월별 추이용 원본 데이터(날짜/타입/금액) — DB별 날짜 포맷 함수 의존을 피하고 서비스 계층에서 월 단위로 집계
    @Query("""
            SELECT t.transactionDate, t.type, t.amount
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.transactionDate BETWEEN :start AND :end
            """)
    List<Object[]> findRawForTrend(@Param("userId") Long userId,
                                    @Param("start") LocalDate start,
                                    @Param("end") LocalDate end);
}
