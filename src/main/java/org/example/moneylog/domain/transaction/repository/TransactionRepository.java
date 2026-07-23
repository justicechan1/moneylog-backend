package org.example.moneylog.domain.transaction.repository;

import org.example.moneylog.domain.transaction.entitiy.Transaction;
import org.example.moneylog.domain.user.entitiy.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 본인 데이터 + 기간(월) 필터 + 페이징
    Page<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end, Pageable pageable);

    // 통계용: 기간 내 전체 조회(페이징 없이)
    List<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end);

    // 단건 조회 시에도 소유자 검증을 함께
    java.util.Optional<Transaction> findByIdAndUser(Long id, User user);
}
