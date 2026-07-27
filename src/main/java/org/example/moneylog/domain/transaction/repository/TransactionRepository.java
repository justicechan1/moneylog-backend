package org.example.moneylog.domain.transaction.repository;

import org.example.moneylog.domain.category.entitiy.Category;
import org.example.moneylog.domain.category.entitiy.CategoryType;
import org.example.moneylog.domain.transaction.entitiy.Transaction;
import org.example.moneylog.domain.user.entitiy.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    // 본인 데이터 + 기간(월) 필터 + 페이징
    Page<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end, Pageable pageable);

    // 통계용: 기간 내 전체 조회(페이징 없이)
    List<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end);

    // 단건 조회 시에도 소유자 검증을 함께
    Optional<Transaction> findByIdAndUser(Long id, User user);

    // 목록 조회: 본인 데이터 + 기간(월) + (선택)타입/카테고리 + 페이징
    @Query("""
            SELECT t FROM Transaction t
            WHERE t.user.id = :userId
              AND t.transactionDate BETWEEN :start AND :end
              AND (:type IS NULL OR t.type = :type)
              AND (:categoryId IS NULL OR t.category.id = :categoryId)
            """)
    Page<Transaction> search(@Param("userId") Long userId,
                              @Param("start") LocalDate start,
                              @Param("end") LocalDate end,
                              @Param("type") CategoryType type,
                              @Param("categoryId") Long categoryId,
                              Pageable pageable);
}
